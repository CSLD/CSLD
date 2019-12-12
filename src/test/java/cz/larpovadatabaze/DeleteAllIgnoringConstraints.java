/*
 *
 * The DbUnit Database Testing Framework
 * Copyright (C)2002-2004, DbUnit.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package cz.larpovadatabaze;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.statement.IBatchStatement;
import org.dbunit.database.statement.IStatementFactory;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITableIterator;
import org.dbunit.dataset.ITableMetaData;
import org.dbunit.operation.AbstractOperation;
import org.dbunit.operation.DeleteAllOperation;
import org.dbunit.operation.TruncateTableOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

/**
 * Deletes all rows of tables present in the specified dataset. If the dataset
 * does not contains a particular table, but that table exists in the database,
 * the database table is not affected. Table are truncated in
 * reverse sequence.
 * <p/>
 * This operation has the same effect of as {@link TruncateTableOperation}.
 * TruncateTableOperation is faster, and it is non-logged, meaning it cannot be
 * rollback. DeleteAllOperation is more portable because not all database vendor
 * support TRUNCATE_TABLE TABLE statement.
 *
 * @author Manuel Laflamme
 * @version $Revision$
 * @see TruncateTableOperation
 * @since Feb 18, 2002
 */
public class DeleteAllIgnoringConstraints extends AbstractOperation
{

    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(DeleteAllOperation.class);

    protected String getDeleteAllCommand()
    {
        return "truncate ";
    }

    ////////////////////////////////////////////////////////////////////////////
    // DatabaseOperation class

    public void execute(IDatabaseConnection connection, IDataSet dataSet)
            throws DatabaseUnitException, SQLException
    {
        logger.debug("execute(connection={}, dataSet={}) - start", connection, dataSet);

        IDataSet databaseDataSet = connection.createDataSet();

        DatabaseConfig databaseConfig = connection.getConfig();
        IStatementFactory statementFactory = (IStatementFactory)databaseConfig.getProperty(DatabaseConfig.PROPERTY_STATEMENT_FACTORY);
        IBatchStatement statement = statementFactory.createBatchStatement(connection);
        try
        {
            int count = 0;

            Stack tableNames = new Stack();
            Set tablesSeen = new HashSet();
            ITableIterator iterator = dataSet.iterator();
            while (iterator.next())
            {
                String tableName = iterator.getTableMetaData().getTableName();
                if (!tablesSeen.contains(tableName))
                {
                    tableNames.push(tableName);
                    tablesSeen.add(tableName);
                }
            }

            // delete tables once each in reverse order of seeing them.
            while (!tableNames.isEmpty())
            {
                String tableName = (String)tableNames.pop();

                // Use database table name. Required to support case sensitive database.
                ITableMetaData databaseMetaData = databaseDataSet.getTableMetaData(tableName);
                tableName = databaseMetaData.getTableName();

                StringBuffer sqlBuffer = new StringBuffer(128);
                sqlBuffer.append(getDeleteAllCommand());
                sqlBuffer.append(getQualifiedName(connection.getSchema(), tableName, connection));
                sqlBuffer.append(" CASCADE;");
                String sql = sqlBuffer.toString();
                statement.addBatch(sql);

                if(logger.isDebugEnabled())
                    logger.debug("Added SQL: {}", sql);

                count++;
            }

            if (count > 0)
            {
                statement.executeBatch();
                statement.clearBatch();
            }
        }
        finally
        {
            statement.close();
        }
    }
}

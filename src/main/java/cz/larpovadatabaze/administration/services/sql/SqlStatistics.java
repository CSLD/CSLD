package cz.larpovadatabaze.administration.services.sql;

import cz.larpovadatabaze.administration.model.MonthlyAmountsStatisticsDto;
import cz.larpovadatabaze.administration.model.RatingStatisticsDto;
import cz.larpovadatabaze.administration.services.Statistics;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class SqlStatistics implements Statistics {
    private SessionFactory sessionFactory;

    @Autowired
    public SqlStatistics(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<RatingStatisticsDto> amountAndRatingsPerMonth() {
        Session currentSession = sessionFactory.getCurrentSession();
        Query ratingRelatedStats = currentSession.createSQLQuery("" +
                "select " +
                "   extract (year from added) as year, " +
                "   extract (month from added) as month, " +
                "   avg(rating) as average_rating," +
                "   count(*) as amount " +
                "from csld_rating " +
                "   WHERE rating IS NOT NULL" +
                "   group by year, month " +
                "   order by year, month;")
                .addScalar("year")
                .addScalar("month")
                .addScalar("amount")
                .addScalar("average_rating")
                .setResultTransformer(Transformers.aliasToBean(RatingStatisticsDto.class));
        return (List<RatingStatisticsDto>) ratingRelatedStats.list();
    }

    @Override
    public List<MonthlyAmountsStatisticsDto> amountOfCommentsPerMonth() {
        Session currentSession = sessionFactory.getCurrentSession();
        Query monthlyCommentsStats = currentSession.createSQLQuery("" +
                "select " +
                "   extract (year from added) as year, " +
                "   extract (month from added) as month, " +
                "   count(*) as amount " +
                "from csld_comment " +
                "   group by year, month " +
                "   order by year, month;")
                .addScalar("year")
                .addScalar("month")
                .addScalar("amount")
                .setResultTransformer(Transformers.aliasToBean(MonthlyAmountsStatisticsDto.class));
        return (List<MonthlyAmountsStatisticsDto>) monthlyCommentsStats.list();
    }
}

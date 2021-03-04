# Deploy to the new production
#   Set maintenance - https://larpdb-maintenance.s3.eu-central-1.amazonaws.com/FullMaintenancePage.html

#   Copy the files.
#   Prepare the database localy
#   Move the database to heroku

#   Move the data

# Run the new production
#   Switch the path to the new production

# Under root acount.
sudo su
cd /home/csld/files
# export AWS Credentials
export AWS_ACCESS_KEY_ID=sdfsdf
export AWS_SECRET_ACCESS_KEY=sdfsdfsd
~/.local/bin/aws s3 cp production/ s3://larpdb-master/ --recursive --region eu-central-1
# Transfer the database.
pg_dump -Fc --no-acl --no-owner -h localhost -U csld csld > csld.dump
~/.local/bin/aws s3 cp csld.dump s3://larpdb-master/csld.dump --region eu-central-1
~/.local/bin/aws s3 presign s3://larpdb-master/csld.dump --region eu-central-1
pg_dump --column-inserts --data-only -U csld csld > csld_dump.sql
heroku pg:reset DATABASE
heroku pg:psql --app larp-db-master
# \i csld_dump.sql

# Copy the presigned URL and DATABASE_URL to next command.
heroku pg:backups:restore 'https://larpdb-master.s3.amazonaws.com/csld.dump?X-Amz-Expires=3600&X-Amz-SignedHeaders=host&X-Amz-Date=20191218T143531Z&X-Amz-Credential=AKIA55UWCRHVHYF7QW73%2F20191218%2Feu-central-1%2Fs3%2Faws4_request&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Signature=4d67558edac750f01f40eb923c46ee9261c1dcbfc8b4a84891e2d398a9ae6d41' DATABASE_URL --app larp-db-master
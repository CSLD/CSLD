# Remove current bucket
aws s3 rb s3://larpdb-cloudnine --force
# Create the bucket via console
# Bucket name: larpdb-cloudnine
aws s3 sync s3://larpdb-master s3://larpdb-cloudnine

# Download the backup from Heroku
heroku pg:backups:capture -a larp-db-master
heroku pg:backups:download -a larp-db-master
pg_restore --verbose --clean --no-acl --no-owner -h localhost -U csld -d csld latest.dump
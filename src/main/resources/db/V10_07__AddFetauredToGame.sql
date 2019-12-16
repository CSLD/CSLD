ALTER TABLE csld_photo ADD COLUMN featured BOOLEAN DEFAULT false;

update csld_photo set featured = true;

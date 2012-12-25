CREATE TABLE role_has_resource (
  role_id INT NOT NULL,
  resource_id INT NOT NULL,

  FOREIGN KEY (role_id) REFERENCES role(id) ON UPDATE CASCADE ON DELETE RESTRICT,
  FOREIGN KEY (resource_id) REFERENCES resource(id) ON UPDATE CASCADE ON DELETE RESTRICT,

  PRIMARY KEY(role_id, resource_id)
);
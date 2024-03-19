CREATE TABLE colaborador (
  id bigint NOT NULL AUTO_INCREMENT,
  full_name varchar(255) DEFAULT NULL,
  password varchar(255) DEFAULT NULL,
  score_password int DEFAULT NULL,
  manager_id bigint DEFAULT NULL,
  PRIMARY KEY (id),
  KEY `FKspmhlca1ok2hm5idxux4h6uae` (manager_id),
  CONSTRAINT `FKspmhlca1ok2hm5idxux4h6uae` FOREIGN KEY (manager_id) REFERENCES colaborador (id)
);
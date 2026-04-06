create table tblauditlog
(
    audit_id          SERIAL PRIMARY KEY,
    auditdate		  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	user_name		  VARCHAR(255),
	user_id		  	  bigint,
	employee_name	  VARCHAR(255),
	employee_id		  bigint,
	module			  VARCHAR(255),
	operation		  VARCHAR(255),
	ip_address		  VARCHAR(255),
	remark			  LONGTEXT,
	entity_ref_id	  BIGINT,
	partner_id	  BIGINT
);
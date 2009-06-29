CREATE TABLE Blog (
	blogId				VARCHAR(256)	NOT NULL,
	userId				VARCHAR(256)	NOT NULL,
	url					VARCHAR(256)	NOT NULL,
	title				VARCHAR(256)	NOT NULL,
	description			VARCHAR(256)	NOT NULL,
	CONSTRAINT Blog_pk PRIMARY KEY (blogId)
);

CREATE TABLE Author (
	userId				VARCHAR(256)	NOT NULL,
	name				VARCHAR(256)	NOT NULL,
	url					VARCHAR(256)	NOT NULL,
	email				VARCHAR(256),
	avatar				VARCHAR(256),
	CONSTRAINT Author_pk PRIMARY KEY (userId)
);

CREATE TABLE Entry (
	entryId				BIGINT			NOT NULL,
	blogId				VARCHAR(256)	NOT NULL,
	title				VARCHAR(256)	NOT NULL,
	postId				BIGINT			NOT NULL,
	tags				VARCHAR(256)   NOT NULL, 
	CONSTRAINT Entry_pk PRIMARY KEY (entryId)
);
CREATE INDEX Entry_entryid_idx ON Entry (entryId);

CREATE TABLE Post (
	postId				BIGINT			NOT NULL,
	userId				VARCHAR(256)	NOT NULL,
	content				LONGVARCHAR		NOT NULL,
	creation			DATE			 NOT NULL,
	CONSTRAINT Post_pk PRIMARY KEY (postId)
);
CREATE INDEX Post_postid_idx ON Entry (postId);

CREATE TABLE Comment (
	commentId			INTEGER			NOT NULL,
	entryId				BIGINT			NOT NULL,
	postId				BIGINT			NOT NULL,
	CONSTRAINT Comment_pk PRIMARY KEY (commentId)
);
CREATE INDEX Comment_commentid_idx ON Comment (commentId);
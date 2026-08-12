-- ALTER TABLE: 为 sys_user 表添加学号/工号列
ALTER TABLE sys_user ADD COLUMN student_number VARCHAR(50) DEFAULT NULL COMMENT '学号/工号' AFTER real_name;

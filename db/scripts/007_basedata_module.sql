/*
 * MIT License
 *
 * Copyright (c) 2026 Employment Tracking System
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

-- ============================================================
-- 007_basedata_module.sql
-- 基础信息模块表结构增量 + 种子数据
-- 1. 为 department/major/class_info/graduate/sys_user/employment_record 补齐模块所需列
-- 2. 就业状态取值统一为中文（与基础信息模块统计口径一致）
-- 3. 种子数据：班级 / 毕业生 / 教师账号 / 学生账号
-- 说明：class_info / graduate 无唯一键，种子数据请仅首次执行；
--       sys_user 使用 INSERT...ON DUPLICATE KEY 幂等写入。
-- ============================================================
USE employment_tracking;

-- 1. 表结构增量 ------------------------------------------------
-- sys_user: 逻辑删除
ALTER TABLE sys_user ADD COLUMN deleted TINYINT DEFAULT 0 COMMENT '逻辑删除: 0正常 1删除' AFTER status;

-- department: 简介 + 逻辑删除
ALTER TABLE department ADD COLUMN description VARCHAR(500) DEFAULT NULL COMMENT '院系简介' AFTER status;
ALTER TABLE department ADD COLUMN deleted TINYINT DEFAULT 0 COMMENT '逻辑删除: 0正常 1删除' AFTER description;

-- major: 简介 + 逻辑删除
ALTER TABLE major ADD COLUMN description VARCHAR(500) DEFAULT NULL COMMENT '专业简介' AFTER status;
ALTER TABLE major ADD COLUMN deleted TINYINT DEFAULT 0 COMMENT '逻辑删除: 0正常 1删除' AFTER description;

-- class_info: 入学/毕业年份 + 简介 + 逻辑删除
ALTER TABLE class_info ADD COLUMN enrollment_year INT DEFAULT NULL COMMENT '入学年份' AFTER status;
ALTER TABLE class_info ADD COLUMN graduation_year INT DEFAULT NULL COMMENT '毕业年份' AFTER enrollment_year;
ALTER TABLE class_info ADD COLUMN description VARCHAR(500) DEFAULT NULL COMMENT '班级简介' AFTER graduation_year;
ALTER TABLE class_info ADD COLUMN deleted TINYINT DEFAULT 0 COMMENT '逻辑删除: 0正常 1删除' AFTER description;

-- graduate: 毕业生状态 + 逻辑删除
ALTER TABLE graduate ADD COLUMN graduate_status TINYINT DEFAULT 1 COMMENT '毕业生状态: 1正常 0异常' AFTER employment_status;
ALTER TABLE graduate ADD COLUMN deleted TINYINT DEFAULT 0 COMMENT '逻辑删除: 0正常 1删除' AFTER graduate_status;

-- employment_record: 佐证材料 + 两级审核字段（兼容基础信息模块毕业生详情）
ALTER TABLE employment_record ADD COLUMN evidence_url VARCHAR(500) DEFAULT NULL COMMENT '佐证材料URL' AFTER submitter_name;
ALTER TABLE employment_record ADD COLUMN stage VARCHAR(30) DEFAULT NULL COMMENT '审核环节: TEACHER_REVIEW/ADMIN_REVIEW/COMPLETED/REJECTED' AFTER evidence_url;
ALTER TABLE employment_record ADD COLUMN teacher_comment VARCHAR(500) DEFAULT NULL COMMENT '教师审核意见' AFTER stage;
ALTER TABLE employment_record ADD COLUMN admin_comment VARCHAR(500) DEFAULT NULL COMMENT '管理员审核意见' AFTER teacher_comment;
ALTER TABLE employment_record ADD COLUMN teacher_review_time DATETIME DEFAULT NULL COMMENT '教师审核时间' AFTER admin_comment;
ALTER TABLE employment_record ADD COLUMN admin_review_time DATETIME DEFAULT NULL COMMENT '管理员审核时间' AFTER teacher_review_time;

-- 2. 就业状态取值统一为中文 ------------------------------------
UPDATE graduate SET employment_status = '已签约'   WHERE employment_status IN ('EMPLOYED','SIGNED');
UPDATE graduate SET employment_status = '升学'     WHERE employment_status IN ('STUDYING','FURTHER_STUDY');
UPDATE graduate SET employment_status = '待就业'   WHERE employment_status IN ('UNEMPLOYED','WAITING');
UPDATE graduate SET employment_status = '灵活就业' WHERE employment_status = 'FLEXIBLE';
UPDATE graduate SET employment_status = '创业'     WHERE employment_status IN ('ENTREPRENEURSHIP','SELF_EMPLOYED');

-- 3. 班级种子数据（24 个，首次执行） ---------------------------
INSERT INTO class_info (name, major_id, grade, sort_order, status, enrollment_year, graduation_year, description) VALUES
('2022级计算机科学与技术1班', 1, '2022', 1, 1, 2022, 2026, '计算机科学与技术专业2022级1班'),
('2023级计算机科学与技术1班', 1, '2023', 2, 1, 2023, 2027, '计算机科学与技术专业2023级1班'),
('2022级软件工程1班', 2, '2022', 1, 1, 2022, 2026, '软件工程专业2022级1班'),
('2023级软件工程1班', 2, '2023', 2, 1, 2023, 2027, '软件工程专业2023级1班'),
('2022级物联网工程1班', 3, '2022', 1, 1, 2022, 2026, '物联网工程专业2022级1班'),
('2023级物联网工程1班', 3, '2023', 2, 1, 2023, 2027, '物联网工程专业2023级1班'),
('2022级电子信息工程1班', 4, '2022', 1, 1, 2022, 2026, '电子信息工程专业2022级1班'),
('2023级电子信息工程1班', 4, '2023', 2, 1, 2023, 2027, '电子信息工程专业2023级1班'),
('2022级通信工程1班', 5, '2022', 1, 1, 2022, 2026, '通信工程专业2022级1班'),
('2023级通信工程1班', 5, '2023', 2, 1, 2023, 2027, '通信工程专业2023级1班'),
('2022级机械设计制造及其自动化1班', 6, '2022', 1, 1, 2022, 2026, '机械设计制造及其自动化专业2022级1班'),
('2023级机械设计制造及其自动化1班', 6, '2023', 2, 1, 2023, 2027, '机械设计制造及其自动化专业2023级1班'),
('2022级车辆工程1班', 7, '2022', 1, 1, 2022, 2026, '车辆工程专业2022级1班'),
('2023级车辆工程1班', 7, '2023', 2, 1, 2023, 2027, '车辆工程专业2023级1班'),
('2022级工商管理1班', 8, '2022', 1, 1, 2022, 2026, '工商管理专业2022级1班'),
('2023级工商管理1班', 8, '2023', 2, 1, 2023, 2027, '工商管理专业2023级1班'),
('2022级会计学1班', 9, '2022', 1, 1, 2022, 2026, '会计学专业2022级1班'),
('2023级会计学1班', 9, '2023', 2, 1, 2023, 2027, '会计学专业2023级1班'),
('2022级法学1班', 10, '2022', 1, 1, 2022, 2026, '法学专业2022级1班'),
('2023级法学1班', 10, '2023', 2, 1, 2023, 2027, '法学专业2023级1班'),
('2022级视觉传达设计1班', 11, '2022', 1, 1, 2022, 2026, '视觉传达设计专业2022级1班'),
('2023级视觉传达设计1班', 11, '2023', 2, 1, 2023, 2027, '视觉传达设计专业2023级1班'),
('2022级数学与应用数学1班', 12, '2022', 1, 1, 2022, 2026, '数学与应用数学专业2022级1班'),
('2023级数学与应用数学1班', 12, '2023', 2, 1, 2023, 2027, '数学与应用数学专业2023级1班');

-- 4. 毕业生种子数据（64 名，首次执行） -------------------------
-- 就业状态使用中文取值：已签约/升学/待就业/灵活就业/创业/出国
INSERT INTO graduate (student_no, name, gender, id_card, phone, email, dept_id, major_id, class_id, grade, graduate_year, employment_status) VALUES
-- 计算机学院 计算机科学与技术 (dept1 major1 class1)
('2022010101','张伟','男','110101200401011234','13800001101','zhangwei@stu.edu.cn',1,1,1,'2022','2026','已签约'),
('2022010102','李娜','女','110101200402022345','13800001102','lina@stu.edu.cn',1,1,1,'2022','2026','已签约'),
('2022010103','王芳','女','110101200403033456','13800001103','wangfang@stu.edu.cn',1,1,1,'2022','2026','升学'),
('2022010104','刘强','男','110101200404044567','13800001104','liuqiang@stu.edu.cn',1,1,1,'2022','2026','待就业'),
-- 计算机学院 软件工程 (dept1 major2 class3)
('2022020101','陈静','女','110101200401055678','13800001201','chenjing@stu.edu.cn',1,2,3,'2022','2026','已签约'),
('2022020102','杨洋','男','110101200402066789','13800001202','yangyang@stu.edu.cn',1,2,3,'2022','2026','已签约'),
('2022020103','赵磊','男','110101200403077890','13800001203','zhaolei@stu.edu.cn',1,2,3,'2022','2026','灵活就业'),
('2022020104','孙丽','女','110101200404088901','13800001204','sunli@stu.edu.cn',1,2,3,'2022','2026','待就业'),
-- 计算机学院 物联网工程 (dept1 major3 class5)
('2022030101','周杰','男','110101200401099012','13800001301','zhoujie@stu.edu.cn',1,3,5,'2022','2026','已签约'),
('2022030102','吴敏','女','110101200402010123','13800001302','wumin@stu.edu.cn',1,3,5,'2022','2026','已签约'),
('2022030103','郑浩','男','110101200403021234','13800001303','zhenghao@stu.edu.cn',1,3,5,'2022','2026','待就业'),
('2022030104','冯雪','女','110101200404032345','13800001304','fengxue@stu.edu.cn',1,3,5,'2022','2026','升学'),
-- 电子信息工程学院 电子信息工程 (dept2 major4 class7)
('2022040101','蒋宇','男','110101200401043456','13800001401','jiangyu@stu.edu.cn',2,4,7,'2022','2026','已签约'),
('2022040102','韩梅','女','110101200402054567','13800001402','hanmei@stu.edu.cn',2,4,7,'2022','2026','已签约'),
('2022040103','曹斌','男','110101200403065678','13800001403','caobin@stu.edu.cn',2,4,7,'2022','2026','待就业'),
('2022040104','邓婷','女','110101200404076789','13800001404','dengting@stu.edu.cn',2,4,7,'2022','2026','灵活就业'),
-- 电子信息工程学院 通信工程 (dept2 major5 class9)
('2022050101','许涛','男','110101200401087890','13800001501','xutao@stu.edu.cn',2,5,9,'2022','2026','已签约'),
('2022050102','沈洁','女','110101200402098901','13800001502','shenjie@stu.edu.cn',2,5,9,'2022','2026','升学'),
('2022050103','马超','男','110101200403009012','13800001503','machao@stu.edu.cn',2,5,9,'2022','2026','待就业'),
('2022050104','唐琳','女','110101200404010123','13800001504','tanglin@stu.edu.cn',2,5,9,'2022','2026','已签约'),
-- 机械工程学院 机械设计制造 (dept3 major6 class11)
('2022060101','卢军','男','110101200401021234','13800001601','lujun@stu.edu.cn',3,6,11,'2022','2026','已签约'),
('2022060102','贺敏','女','110101200402032345','13800001602','hemin@stu.edu.cn',3,6,11,'2022','2026','待就业'),
('2022060103','龚鹏','男','110101200403043456','13800001603','gongpeng@stu.edu.cn',3,6,11,'2022','2026','已签约'),
('2022060104','范霞','女','110101200404054567','13800001604','fanxia@stu.edu.cn',3,6,11,'2022','2026','灵活就业'),
-- 机械工程学院 车辆工程 (dept3 major7 class13)
('2022070101','邱波','男','110101200401065678','13800001701','qiubo@stu.edu.cn',3,7,13,'2022','2026','已签约'),
('2022070102','钟燕','女','110101200402076789','13800001702','zhongyan@stu.edu.cn',3,7,13,'2022','2026','升学'),
('2022070103','姜磊','男','110101200403087890','13800001703','jianglei@stu.edu.cn',3,7,13,'2022','2026','待就业'),
('2022070104','崔薇','女','110101200404098901','13800001704','cuiwei@stu.edu.cn',3,7,13,'2022','2026','已签约'),
-- 经济管理学院 工商管理 (dept4 major8 class15)
('2022080101','范鑫','男','110101200401009012','13800001801','fanxin@stu.edu.cn',4,8,15,'2022','2026','已签约'),
('2022080102','石磊','男','110101200402010123','13800001802','shilei@stu.edu.cn',4,8,15,'2022','2026','已签约'),
('2022080103','蔡雯','女','110101200403021234','13800001803','caiwen@stu.edu.cn',4,8,15,'2022','2026','待就业'),
('2022080104','余航','男','110101200404032345','13800001804','yuhang@stu.edu.cn',4,8,15,'2022','2026','灵活就业'),
-- 经济管理学院 会计学 (dept4 major9 class17)
('2022090101','潘婷','女','110101200401043456','13800001901','panting@stu.edu.cn',4,9,17,'2022','2026','已签约'),
('2022090102','杜宇','男','110101200402054567','13800001902','duyu@stu.edu.cn',4,9,17,'2022','2026','升学'),
('2022090103','夏琳','女','110101200403065678','13800001903','xialin@stu.edu.cn',4,9,17,'2022','2026','已签约'),
('2022090104','田刚','男','110101200404076789','13800001904','tiangang@stu.edu.cn',4,9,17,'2022','2026','待就业'),
-- 文法学院 法学 (dept5 major10 class19)
('2022100101','方圆','女','110101200401087890','13800002001','fangyuan@stu.edu.cn',5,10,19,'2022','2026','已签约'),
('2022100102','汪洋','男','110101200402098901','13800002002','wangyang@stu.edu.cn',5,10,19,'2022','2026','待就业'),
('2022100103','廖静','女','110101200403009012','13800002003','liaojing@stu.edu.cn',5,10,19,'2022','2026','已签约'),
('2022100104','邹凯','男','110101200404010123','13800002004','zoukai@stu.edu.cn',5,10,19,'2022','2026','升学'),
-- 艺术设计学院 视觉传达设计 (dept6 major11 class21)
('2022110101','熊伟','男','110101200401021234','13800002101','xiongwei@stu.edu.cn',6,11,21,'2022','2026','已签约'),
('2022110102','龙梅','女','110101200402032345','13800002102','longmei@stu.edu.cn',6,11,21,'2022','2026','已签约'),
('2022110103','龚强','男','110101200403043456','13800002103','gongqiang@stu.edu.cn',6,11,21,'2022','2026','灵活就业'),
('2022110104','段丽','女','110101200404054567','13800002104','duanli@stu.edu.cn',6,11,21,'2022','2026','待就业'),
-- 理学院 数学与应用数学 (dept7 major12 class23)
('2022120101','黎明','男','110101200401065678','13800002201','liming@stu.edu.cn',7,12,23,'2022','2026','已签约'),
('2022120102','舒婷','女','110101200402076789','13800002202','shuting@stu.edu.cn',7,12,23,'2022','2026','升学'),
('2022120103','尹航','男','110101200403087890','13800002203','yinhang@stu.edu.cn',7,12,23,'2022','2026','已签约'),
('2022120104','易帆','女','110101200404098901','13800002204','yifan@stu.edu.cn',7,12,23,'2022','2026','待就业');

-- 5. 教师账号（其余 6 个院系各 1 位，密码 123456，幂等） ----------
INSERT INTO sys_user (username, student_number, password, real_name, phone, email, role, dept_id, status) VALUES
('teacher03', 'teacher03', '$2a$10$XV0FVww7JuwkobPtcF5rCOQLuT2jCwmKERGIB2aRFpP2Vd43DyUuu', '刘慧敏', '13800000003', 'liuhm@college.edu.cn', 'TEACHER', 2, 1),
('teacher04', 'teacher04', '$2a$10$XV0FVww7JuwkobPtcF5rCOQLuT2jCwmKERGIB2aRFpP2Vd43DyUuu', '陈志强', '13800000004', 'chenzq@college.edu.cn', 'TEACHER', 3, 1),
('teacher05', 'teacher05', '$2a$10$XV0FVww7JuwkobPtcF5rCOQLuT2jCwmKERGIB2aRFpP2Vd43DyUuu', '赵丽娟', '13800000005', 'zhaolj@college.edu.cn', 'TEACHER', 4, 1),
('teacher06', 'teacher06', '$2a$10$XV0FVww7JuwkobPtcF5rCOQLuT2jCwmKERGIB2aRFpP2Vd43DyUuu', '孙宏伟', '13800000006', 'sunhw@college.edu.cn', 'TEACHER', 5, 1),
('teacher07', 'teacher07', '$2a$10$XV0FVww7JuwkobPtcF5rCOQLuT2jCwmKERGIB2aRFpP2Vd43DyUuu', '周雅琴', '13800000007', 'zhouyq@college.edu.cn', 'TEACHER', 6, 1),
('teacher08', 'teacher08', '$2a$10$XV0FVww7JuwkobPtcF5rCOQLuT2jCwmKERGIB2aRFpP2Vd43DyUuu', '吴国栋', '13800000008', 'wugd@college.edu.cn', 'TEACHER', 7, 1)
ON DUPLICATE KEY UPDATE username = username;

-- 6. 学生账号（为毕业生创建 GRADUATE 账号，用户名=学号，密码 123456，幂等） --
INSERT INTO sys_user (username, student_number, password, real_name, phone, email, dept_id, role, status)
SELECT g.student_no, g.student_no, '$2a$10$XV0FVww7JuwkobPtcF5rCOQLuT2jCwmKERGIB2aRFpP2Vd43DyUuu', g.name, g.phone, g.email, g.dept_id, 'GRADUATE', 1
FROM graduate g
WHERE NOT EXISTS (SELECT 1 FROM sys_user u WHERE u.username = g.student_no);

-- 7. 确认结果
SELECT 'department=' AS info, COUNT(*) FROM department
UNION ALL SELECT 'major=', COUNT(*) FROM major
UNION ALL SELECT 'class_info=', COUNT(*) FROM class_info
UNION ALL SELECT 'graduate=', COUNT(*) FROM graduate
UNION ALL SELECT 'teacher=', COUNT(*) FROM sys_user WHERE role = 'TEACHER'
UNION ALL SELECT 'graduate_account=', COUNT(*) FROM sys_user WHERE role = 'GRADUATE';

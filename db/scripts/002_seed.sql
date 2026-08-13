-- ==========================================
-- 就业跟踪系统 - 初始化种子数据
-- ==========================================


-- 默认管理员账号由 DataInitializer 在应用启动时自动创建
-- 用户名: admin  密码: admin123

-- 院系数据
INSERT INTO department (name, code, sort_order) VALUES
('计算机科学与技术学院', 'CS', 1),
('电子信息工程学院',   'EE', 2),
('机械工程学院',       'ME', 3),
('经济管理学院',       'EM', 4),
('文法学院',           'LA', 5),
('艺术设计学院',       'AD', 6),
('理学院',             'SC', 7);

-- 专业数据
INSERT INTO major (name, code, dept_id, sort_order) VALUES
('计算机科学与技术', 'CS01', 1, 1),
('软件工程',         'CS02', 1, 2),
('物联网工程',       'CS03', 1, 3),
('电子信息工程',     'EE01', 2, 1),
('通信工程',         'EE02', 2, 2),
('机械设计制造',     'ME01', 3, 1),
('车辆工程',         'ME02', 3, 2),
('工商管理',         'EM01', 4, 1),
('会计学',           'EM02', 4, 2),
('法学',             'LA01', 5, 1),
('视觉传达设计',     'AD01', 6, 1),
('数学与应用数学',   'SC01', 7, 1);

-- 字典类型
INSERT INTO dict_type (code, name) VALUES
('employment_status', '就业状态'),
('review_status', '审核状态'),
('company_type', '单位性质'),
('industry', '所属行业'),
('destination', '去向类型');

-- 字典项 - 就业状态
INSERT INTO dict_item (type_id, label, value, sort_order) VALUES
(1, '已就业', 'EMPLOYED', 1),
(1, '未就业', 'UNEMPLOYED', 2),
(1, '升学', 'FURTHER_STUDY', 3),
(1, '入伍', 'MILITARY', 4),
(1, '创业', 'ENTREPRENEURSHIP', 5),
(1, '待业', 'WAITING', 6);

-- 字典项 - 审核状态
INSERT INTO dict_item (type_id, label, value, sort_order) VALUES
(2, '待审核', 'PENDING', 1),
(2, '审核通过', 'PASSED', 2),
(2, '审核退回', 'REJECTED', 3);

-- 字典项 - 去向类型
INSERT INTO dict_item (type_id, label, value, sort_order) VALUES
(5, '签约就业', 'SIGNED', 1),
(5, '升学深造', 'FURTHER_STUDY', 2),
(5, '出国留学', 'ABROAD', 3),
(5, '自主创业', 'ENTREPRENEURSHIP', 4),
(5, '灵活就业', 'FLEXIBLE', 5),
(5, '待就业', 'WAITING', 6);

-- 字典项 - 单位性质
INSERT INTO dict_item (type_id, label, value, sort_order) VALUES
(3, '国有企业', 'STATE_OWNED', 1),
(3, '民营企业', 'PRIVATE', 2),
(3, '外资企业', 'FOREIGN', 3),
(3, '事业单位', 'INSTITUTION', 4),
(3, '政府机关', 'GOVERNMENT', 5);

-- 字典项 - 所属行业
INSERT INTO dict_item (type_id, label, value, sort_order) VALUES
(4, '互联网/IT', 'IT', 1),
(4, '金融', 'FINANCE', 2),
(4, '教育', 'EDUCATION', 3),
(4, '制造业', 'MANUFACTURING', 4),
(4, '医疗健康', 'HEALTHCARE', 5),
(4, '房地产/建筑', 'REAL_ESTATE', 6);

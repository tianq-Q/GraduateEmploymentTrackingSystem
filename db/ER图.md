# ER 图

14 张表关系说明：

## 核心业务
department ──┬─> major ──┬─> class_info ──> graduate ──> employment_record
sys_user    ──┘           └──────────────> graduate
              └─> employment_record (reviewer)

## 支撑
employment_record ──> attachment (一对多)
employment_record ──> audit_log (一对多)
dict_type ──> dict_item (一对多)

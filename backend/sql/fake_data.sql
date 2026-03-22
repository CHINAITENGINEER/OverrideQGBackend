-- =============================================
-- 宿舍报修管理系统 假数据脚本
-- 密码均为 admin123 的 BCrypt 加密结果
-- =============================================

USE dormitory_repair;

-- 清空现有数据（可选，谨慎使用）
-- DELETE FROM repair_order;
-- DELETE FROM user;
-- ALTER TABLE user AUTO_INCREMENT = 1;
-- ALTER TABLE repair_order AUTO_INCREMENT = 1;

-- =============================================
-- 管理员用户
-- =============================================
INSERT INTO `user` (`account`, `password`, `role`) VALUES
('0025000001', '$2a$10$3OTDwBGABqefixZJyANMCuLXGfEjgpBbJhMHqIRfDymrF1jCqVDUa', 1),
('0025000002', '$2a$10$3OTDwBGABqefixZJyANMCuLXGfEjgpBbJhMHqIRfDymrF1jCqVDUa', 1),
('0025000003', '$2a$10$3OTDwBGABqefixZJyANMCuLXGfEjgpBbJhMHqIRfDymrF1jCqVDUa', 1);

-- =============================================
-- 学生用户（A栋）
-- =============================================
INSERT INTO `user` (`account`, `password`, `role`, `building`, `room_no`) VALUES
('3125001001', '$2a$10$3OTDwBGABqefixZJyANMCuLXGfEjgpBbJhMHqIRfDymrF1jCqVDUa', 0, 'A栋', '101'),
('3125001002', '$2a$10$3OTDwBGABqefixZJyANMCuLXGfEjgpBbJhMHqIRfDymrF1jCqVDUa', 0, 'A栋', '102'),
('3125001003', '$2a$10$3OTDwBGABqefixZJyANMCuLXGfEjgpBbJhMHqIRfDymrF1jCqVDUa', 0, 'A栋', '103'),
('3125001004', '$2a$10$3OTDwBGABqefixZJyANMCuLXGfEjgpBbJhMHqIRfDymrF1jCqVDUa', 0, 'A栋', '104'),
('3125001005', '$2a$10$3OTDwBGABqefixZJyANMCuLXGfEjgpBbJhMHqIRfDymrF1jCqVDUa', 0, 'A栋', '201'),

-- =============================================
-- 学生用户（B栋）
-- =============================================
('3225002001', '$2a$10$3OTDwBGABqefixZJyANMCuLXGfEjgpBbJhMHqIRfDymrF1jCqVDUa', 0, 'B栋', '101'),
('3225002002', '$2a$10$3OTDwBGABqefixZJyANMCuLXGfEjgpBbJhMHqIRfDymrF1jCqVDUa', 0, 'B栋', '102'),
('3225002003', '$2a$10$3OTDwBGABqefixZJyANMCuLXGfEjgpBbJhMHqIRfDymrF1jCqVDUa', 0, 'B栋', '201'),
('3225002004', '$2a$10$3OTDwBGABqefixZJyANMCuLXGfEjgpBbJhMHqIRfDymrF1jCqVDUa', 0, 'B栋', '202'),

-- =============================================
-- 学生用户（C栋）
-- =============================================
('3125003001', '$2a$10$3OTDwBGABqefixZJyANMCuLXGfEjgpBbJhMHqIRfDymrF1jCqVDUa', 0, 'C栋', '301'),
('3125003002', '$2a$10$3OTDwBGABqefixZJyANMCuLXGfEjgpBbJhMHqIRfDymrF1jCqVDUa', 0, 'C栋', '302'),
('3125003003', '$2a$10$3OTDwBGABqefixZJyANMCuLXGfEjgpBbJhMHqIRfDymrF1jCqVDUa', 0, 'C栋', '303');

-- =============================================
-- 报修单数据
-- =============================================

-- 待处理状态（status=0）
INSERT INTO `repair_order` (`student_id`, `building`, `room_no`, `device_type`, `description`, `priority`, `status`, `created_at`) VALUES
(4, 'A栋', '101', '灯具', '卧室顶灯不亮，需要更换灯泡或检查电路', 1, 0, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(5, 'A栋', '102', '空调', '空调制冷效果差，吹出来的风不冷', 2, 0, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(6, 'A栋', '103', '门锁', '宿舍门锁损坏，钥匙转不动', 3, 0, NOW()),
(7, 'B栋', '101', '水龙头', '浴室水龙头漏水，滴答滴答的声音很烦', 1, 0, DATE_SUB(NOW(), INTERVAL 3 DAY)),
(8, 'B栋', '102', '窗户', '窗户玻璃有裂纹，担心安全问题', 3, 0, DATE_SUB(NOW(), INTERVAL 5 HOUR)),
(9, 'C栋', '301', '床架', '床架摇晃，螺丝松动了', 1, 0, DATE_SUB(NOW(), INTERVAL 12 HOUR));

-- 处理中状态（status=1）
INSERT INTO `repair_order` (`student_id`, `building`, `room_no`, `device_type`, `description`, `priority`, `status`, `admin_id`, `remark`, `created_at`) VALUES
(10, 'A栋', '104', '插座', '插座接触不良，充电器插不稳', 2, 1, 1, '已安排维修人员上门检查', DATE_SUB(NOW(), INTERVAL 6 HOUR)),
(11, 'A栋', '201', '热水器', '热水器温度不够热，洗澡很冷', 2, 1, 2, '正在联系热水器维修部门', DATE_SUB(NOW(), INTERVAL 4 HOUR)),
(12, 'B栋', '201', '地板', '地板有破损，踩上去会扎脚', 2, 1, 1, '已购买地板胶带，明天上午修复', DATE_SUB(NOW(), INTERVAL 2 HOUR)),
(13, 'B栋', '202', '柜子', '衣柜门脱落，无法正常使用', 2, 1, 3, '正在制作新的柜门', DATE_SUB(NOW(), INTERVAL 1 HOUR)),
(14, 'C栋', '302', '网络', '网络信号很弱，经常掉线', 1, 1, 2, '已检查路由器，需要更换天线', DATE_SUB(NOW(), INTERVAL 30 MINUTE));

-- 已完成状态（status=2）- 有评分
INSERT INTO `repair_order` (`student_id`, `building`, `room_no`, `device_type`, `description`, `priority`, `status`, `admin_id`, `remark`, `rating`, `created_at`, `updated_at`) VALUES
(4, 'A栋', '101', '灯具', '走廊灯不亮', 1, 2, 1, '已更换灯泡', 5, DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_SUB(NOW(), INTERVAL 9 DAY)),
(5, 'A栋', '102', '风扇', '风扇叶片损坏', 1, 2, 2, '已更换新风扇', 4, DATE_SUB(NOW(), INTERVAL 8 DAY), DATE_SUB(NOW(), INTERVAL 7 DAY)),
(6, 'A栋', '103', '垃圾桶', '垃圾桶盖子坏了', 1, 2, 1, '已更换垃圾桶', 3, DATE_SUB(NOW(), INTERVAL 6 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY)),
(7, 'B栋', '101', '镜子', '镜子破裂', 2, 2, 3, '已更换新镜子', 5, DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY)),
(8, 'B栋', '102', '毛巾架', '毛巾架脱落', 1, 2, 2, '已重新安装', 4, DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),
(9, 'C栋', '301', '书桌', '书桌抽屉卡住', 1, 2, 1, '已修复抽屉', 5, DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
(10, 'A栋', '104', '椅子', '椅子腿断了', 1, 2, 2, '已更换新椅子', 4, DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY)),
(11, 'A栋', '201', '门把手', '门把手松动', 1, 2, 3, '已紧固门把手', 3, DATE_SUB(NOW(), INTERVAL 7 DAY), DATE_SUB(NOW(), INTERVAL 6 DAY));

-- 已完成状态（status=2）- 无评分
INSERT INTO `repair_order` (`student_id`, `building`, `room_no`, `device_type`, `description`, `priority`, `status`, `admin_id`, `remark`, `created_at`, `updated_at`) VALUES
(12, 'B栋', '201', '灯开关', '灯开关失效', 1, 2, 1, '已更换开关', DATE_SUB(NOW(), INTERVAL 9 DAY), DATE_SUB(NOW(), INTERVAL 8 DAY)),
(13, 'B栋', '202', '地垫', '地垫破损', 1, 2, 2, '已更换地垫', DATE_SUB(NOW(), INTERVAL 8 DAY), DATE_SUB(NOW(), INTERVAL 7 DAY)),
(14, 'C栋', '302', '窗帘', '窗帘杆断裂', 1, 2, 3, '已更换窗帘杆', DATE_SUB(NOW(), INTERVAL 6 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY));

-- 已取消状态（status=3）
INSERT INTO `repair_order` (`student_id`, `building`, `room_no`, `device_type`, `description`, `priority`, `status`, `remark`, `created_at`) VALUES
(4, 'A栋', '101', '其他', '原来是自己操作不当，已自行解决', 1, 3, '学生主动取消', DATE_SUB(NOW(), INTERVAL 15 DAY)),
(5, 'A栋', '102', '其他', '问题已自行修复', 1, 3, '学生主动取消', DATE_SUB(NOW(), INTERVAL 12 DAY)),
(6, 'A栋', '103', '其他', '搬家了，不需要修了', 1, 3, '学生主动取消', DATE_SUB(NOW(), INTERVAL 11 DAY));

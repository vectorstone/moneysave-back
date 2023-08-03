use account_book;
truncate book;
truncate dict;
show create table book;
-- 0802最新
CREATE TABLE `book` (
                        `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'book的id',
                        `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建日期',
                        `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
                        `type` varchar(20) DEFAULT NULL COMMENT '收支类型',
                        `amount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '金额',
                        `category` varchar(20) DEFAULT NULL COMMENT '类别',
                        `subcategory` varchar(20) DEFAULT NULL COMMENT '子类',
                        `account` varchar(100) DEFAULT NULL COMMENT '账户',
                        `book` varchar(100) DEFAULT NULL COMMENT '账本名称',
                        `reimbursement_account` varchar(100) DEFAULT NULL COMMENT '报销账户',
                        `reimbursement_amount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '报销金额',
                        `remark` varchar(100) DEFAULT NULL COMMENT '备注',
                        `label` varchar(100) DEFAULT NULL COMMENT '标签',
                        `address` varchar(100) DEFAULT NULL COMMENT '地址',
                        `user` bigint DEFAULT NULL COMMENT '创建用户的id',
                        `attachment1` varchar(100) DEFAULT NULL COMMENT '附件1的oss路径',
                        `attachment2` varchar(100) DEFAULT NULL COMMENT '附件2的oss路径',
                        `attachment3` varchar(100) DEFAULT NULL COMMENT '附件3的oss路径',
                        `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标记(0:不可用 1:可用)',
                        PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1686562083136630797 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

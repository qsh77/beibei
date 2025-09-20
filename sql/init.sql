-- 贝贝家政服务平台数据库初始化脚本
-- 数据库：beibei
-- 字符集：utf8mb4

SET NAMES utf8mb4;
SET time_zone = '+08:00';

-- 创建数据库
CREATE DATABASE IF NOT EXISTS beibei DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE beibei;

-- 用户表
CREATE TABLE user (
  id            BIGINT PRIMARY KEY AUTO_INCREMENT,
  phone         VARCHAR(20) NOT NULL UNIQUE,
  password_hash VARCHAR(100) NOT NULL,
  role          ENUM('USER','WORKER','ADMIN') NOT NULL DEFAULT 'USER',
  status        TINYINT NOT NULL DEFAULT 1,               -- 1 启用 0 禁用
  created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 用户资料表
CREATE TABLE user_profile (
  user_id     BIGINT PRIMARY KEY,
  name        VARCHAR(50),
  gender      ENUM('M','F','U') DEFAULT 'U',
  birthday    DATE NULL,
  avatar      VARCHAR(255),
  email       VARCHAR(100),
  address     VARCHAR(255),
  CONSTRAINT fk_profile_user FOREIGN KEY (user_id) REFERENCES user(id)
) ENGINE=InnoDB;

-- 用户地址表
CREATE TABLE user_address (
  id           BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id      BIGINT NOT NULL,
  contact_name VARCHAR(50),
  contact_phone VARCHAR(20),
  province     VARCHAR(50), 
  city         VARCHAR(50), 
  district     VARCHAR(50),
  detail       VARCHAR(200),
  is_default   TINYINT NOT NULL DEFAULT 0,
  created_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_addr_user FOREIGN KEY (user_id) REFERENCES user(id),
  INDEX idx_addr_user (user_id, is_default)
) ENGINE=InnoDB;

-- 阿姨表
CREATE TABLE worker (
  id        BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id   BIGINT NOT NULL UNIQUE,
  level     TINYINT DEFAULT 1,
  years     SMALLINT DEFAULT 0,
  bio       VARCHAR(500),
  score     DECIMAL(3,2) NOT NULL DEFAULT 5.00,
  status    TINYINT NOT NULL DEFAULT 1,
  CONSTRAINT fk_worker_user FOREIGN KEY (user_id) REFERENCES user(id)
) ENGINE=InnoDB;

-- 阿姨档期表
CREATE TABLE worker_schedule (
  id         BIGINT PRIMARY KEY AUTO_INCREMENT,
  worker_id  BIGINT NOT NULL,
  date       DATE NOT NULL,
  time_slot  VARCHAR(20) NOT NULL,           -- 如 AM/PM/H09-12
  status     ENUM('FREE','BUSY') NOT NULL DEFAULT 'FREE',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_ws_worker FOREIGN KEY (worker_id) REFERENCES worker(id),
  UNIQUE KEY uk_ws (worker_id, date, time_slot)
) ENGINE=InnoDB;

-- 服务类目表
CREATE TABLE service_category (
  id        BIGINT PRIMARY KEY AUTO_INCREMENT,
  parent_id BIGINT NULL,
  name      VARCHAR(50) NOT NULL,
  sort      INT DEFAULT 0,
  INDEX idx_cat_parent (parent_id, sort)
) ENGINE=InnoDB;

-- 服务表
CREATE TABLE service (
  id          BIGINT PRIMARY KEY AUTO_INCREMENT,
  category_id BIGINT NOT NULL,
  name        VARCHAR(100) NOT NULL,
  description VARCHAR(500),
  base_price  DECIMAL(10,2) NOT NULL,
  unit        ENUM('小时','天','单') NOT NULL,
  status      TINYINT NOT NULL DEFAULT 1,
  hot         TINYINT NOT NULL DEFAULT 0,
  rating      DECIMAL(3,2) DEFAULT 5.00,
  created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_srv_cat FOREIGN KEY (category_id) REFERENCES service_category(id),
  INDEX idx_srv_cat (category_id, status, hot)
) ENGINE=InnoDB;

-- 订单表
CREATE TABLE `order` (
  id            BIGINT PRIMARY KEY AUTO_INCREMENT,
  order_no      VARCHAR(40) NOT NULL UNIQUE,
  customer_id   BIGINT NOT NULL,
  worker_id     BIGINT NOT NULL,
  service_id    BIGINT NOT NULL,
  schedule_date DATE NOT NULL,
  time_slot     VARCHAR(20) NOT NULL,
  address_id    BIGINT,
  address_text  VARCHAR(300),              -- 下单时的地址快照
  amount        DECIMAL(10,2) NOT NULL,
  status        ENUM('CREATED','ASSIGNED','DOING','DONE','CANCELED') NOT NULL DEFAULT 'CREATED',
  remark        VARCHAR(255),
  created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_ord_user    FOREIGN KEY (customer_id) REFERENCES user(id),
  CONSTRAINT fk_ord_worker  FOREIGN KEY (worker_id)  REFERENCES worker(id),
  CONSTRAINT fk_ord_service FOREIGN KEY (service_id) REFERENCES service(id),
  CONSTRAINT fk_ord_addr    FOREIGN KEY (address_id) REFERENCES user_address(id),
  INDEX idx_ord_user (customer_id, status, created_at),
  INDEX idx_ord_worker (worker_id, status, schedule_date),
  INDEX idx_ord_service (service_id)
) ENGINE=InnoDB;

-- 订单日志表
CREATE TABLE order_log (
  id         BIGINT PRIMARY KEY AUTO_INCREMENT,
  order_id   BIGINT NOT NULL,
  from_status ENUM('CREATED','ASSIGNED','DOING','DONE','CANCELED') NULL,
  to_status   ENUM('CREATED','ASSIGNED','DOING','DONE','CANCELED') NOT NULL,
  op_by      BIGINT NULL,                 -- 操作人（用户或管理员）
  note       VARCHAR(255),
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_olog_order FOREIGN KEY (order_id) REFERENCES `order`(id),
  INDEX idx_olog_order (order_id, created_at)
) ENGINE=InnoDB;

-- 评价表
CREATE TABLE review (
  id         BIGINT PRIMARY KEY AUTO_INCREMENT,
  order_id   BIGINT NOT NULL UNIQUE,
  rating     TINYINT NOT NULL CHECK (rating BETWEEN 1 AND 5),
  content    VARCHAR(500),
  imgs       JSON NULL,
  created_by BIGINT NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_review_order FOREIGN KEY (order_id) REFERENCES `order`(id),
  INDEX idx_review_user (created_by, created_at)
) ENGINE=InnoDB;

-- 插入初始数据
-- 管理员用户
INSERT INTO user (phone, password_hash, role, status) VALUES 
('13800000000', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'ADMIN', 1);

-- 服务类目
INSERT INTO service_category (id, parent_id, name, sort) VALUES 
(1, NULL, '家庭保洁', 1),
(2, NULL, '月嫂服务', 2),
(3, NULL, '育儿嫂', 3),
(4, NULL, '钟点工', 4);

-- 服务项目
INSERT INTO service (category_id, name, description, base_price, unit, status, hot, rating) VALUES 
(1, '日常保洁', '客厅、卧室、厨房、卫生间全面清洁', 50.00, '小时', 1, 1, 4.8),
(1, '深度保洁', '包含日常保洁+家电清洁+玻璃清洁', 80.00, '小时', 1, 1, 4.9),
(2, '月嫂服务', '专业月嫂，24小时照顾产妇和新生儿', 300.00, '天', 1, 1, 4.7),
(3, '育儿嫂', '专业育儿嫂，照顾0-3岁婴幼儿', 200.00, '天', 1, 0, 4.6),
(4, '钟点工', '灵活时间，按需服务', 40.00, '小时', 1, 1, 4.5);

-- 测试用户
INSERT INTO user (phone, password_hash, role, status) VALUES 
('13800000001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'USER', 1),
('13800000002', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'WORKER', 1);

-- 用户资料
INSERT INTO user_profile (user_id, name, gender, avatar) VALUES 
(1, '系统管理员', 'U', NULL),
(2, '张女士', 'F', NULL),
(3, '李阿姨', 'F', NULL);

-- 阿姨信息
INSERT INTO worker (user_id, level, years, bio, score, status) VALUES 
(3, 3, 5, '专业月嫂，有5年经验，擅长新生儿护理和产妇护理', 4.8, 1);

-- 阿姨档期（未来7天）
INSERT INTO worker_schedule (worker_id, date, time_slot, status) VALUES 
(1, DATE_ADD(CURDATE(), INTERVAL 1 DAY), 'AM', 'FREE'),
(1, DATE_ADD(CURDATE(), INTERVAL 1 DAY), 'PM', 'FREE'),
(1, DATE_ADD(CURDATE(), INTERVAL 2 DAY), 'AM', 'FREE'),
(1, DATE_ADD(CURDATE(), INTERVAL 2 DAY), 'PM', 'FREE'),
(1, DATE_ADD(CURDATE(), INTERVAL 3 DAY), 'AM', 'FREE'),
(1, DATE_ADD(CURDATE(), INTERVAL 3 DAY), 'PM', 'FREE');

COMMIT;

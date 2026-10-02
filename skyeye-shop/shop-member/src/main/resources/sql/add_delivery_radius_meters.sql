-- 同城配送：门店可覆盖平台默认最大配送半径（米）；NULL 表示沿用平台默认
ALTER TABLE `shop_store`
    ADD COLUMN `delivery_radius_meters` INT NULL COMMENT '同城配送半径（米），空=平台默认' AFTER `latitude`;

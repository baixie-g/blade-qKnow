-- ----------------------------
-- 实体池菜单配置
-- ----------------------------
insert into system_menu values (2100, '实体池管理', 2015, 5, 'entityPool', 'ext/extEntityPool/index', NULL, NULL, 1, 0, 'C', '0', '0', 'ext:extEntityPool:list', '#', '小桐', sysdate(), '', null, '');
insert into system_menu values (2101, '实体池查询', 2100, 1, '#', '', NULL, NULL, 1, 0, 'F', '0', '0', 'ext:extEntityPool:query', '#', '小桐', sysdate(), '', null, '');
insert into system_menu values (2102, '实体池新增', 2100, 2, '#', '', NULL, NULL, 1, 0, 'F', '0', '0', 'ext:extEntityPool:add', '#', '小桐', sysdate(), '', null, '');
insert into system_menu values (2103, '实体池修改', 2100, 3, '#', '', NULL, NULL, 1, 0, 'F', '0', '0', 'ext:extEntityPool:edit', '#', '小桐', sysdate(), '', null, '');
insert into system_menu values (2104, '实体池删除', 2100, 4, '#', '', NULL, NULL, 1, 0, 'F', '0', '0', 'ext:extEntityPool:remove', '#', '小桐', sysdate(), '', null, '');
insert into system_menu values (2105, '实体池导出', 2100, 5, '#', '', NULL, NULL, 1, 0, 'F', '0', '0', 'ext:extEntityPool:export', '#', '小桐', sysdate(), '', null, '');
insert into system_menu values (2106, '实体池处理', 2100, 6, '#', '', NULL, NULL, 1, 0, 'F', '0', '0', 'ext:extEntityPool:process', '#', '小桐', sysdate(), '', null, '');

-- ----------------------------
-- 关系池菜单配置
-- ----------------------------
insert into system_menu values (2110, '关系池管理', 2015, 6, 'relationshipPool', 'ext/extRelationshipPool/index', NULL, NULL, 1, 0, 'C', '0', '0', 'ext:extRelationshipPool:list', '#', '小桐', sysdate(), '', null, '');
insert into system_menu values (2111, '关系池查询', 2110, 1, '#', '', NULL, NULL, 1, 0, 'F', '0', '0', 'ext:extRelationshipPool:query', '#', '小桐', sysdate(), '', null, '');
insert into system_menu values (2112, '关系池新增', 2110, 2, '#', '', NULL, NULL, 1, 0, 'F', '0', '0', 'ext:extRelationshipPool:add', '#', '小桐', sysdate(), '', null, '');
insert into system_menu values (2113, '关系池修改', 2110, 3, '#', '', NULL, NULL, 1, 0, 'F', '0', '0', 'ext:extRelationshipPool:edit', '#', '小桐', sysdate(), '', null, '');
insert into system_menu values (2114, '关系池删除', 2110, 4, '#', '', NULL, NULL, 1, 0, 'F', '0', '0', 'ext:extRelationshipPool:remove', '#', '小桐', sysdate(), '', null, '');
insert into system_menu values (2115, '关系池导出', 2110, 5, '#', '', NULL, NULL, 1, 0, 'F', '0', '0', 'ext:extRelationshipPool:export', '#', '小桐', sysdate(), '', null, '');
insert into system_menu values (2116, '关系池处理', 2110, 6, '#', '', NULL, NULL, 1, 0, 'F', '0', '0', 'ext:extRelationshipPool:process', '#', '小桐', sysdate(), '', null, ''); 
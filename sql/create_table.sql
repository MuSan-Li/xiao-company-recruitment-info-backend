create database my_db;
use my_db;
-- 用户表
create table if not exists t_user
(
    id            bigint auto_increment comment 'id' primary key,
    user_account  varchar(256)                           not null comment '账号',
    user_password varchar(512)                           not null comment '密码',
    user_name     varchar(256)                           null comment '用户昵称',
    user_avatar   varchar(1024)                          null comment '用户头像',
    user_profile  varchar(512)                           null comment '用户简介',
    user_role     varchar(256) default 'user'            not null comment '用户角色：user/admin/ban',
    create_time   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete     tinyint      default 0                 not null comment '是否删除',
    index idx_user_account (user_account)
) comment '用户' collate = utf8mb4_unicode_ci;


create table t_company
(
    id               bigint auto_increment comment 'ID'
        primary key,
    stock_code       varchar(20)                        null comment '股票代码',
    stock_name       varchar(50)                        null comment '股票名称',
    name             varchar(256)                       null comment '公司全称',
    time_to_market   date                               null comment '上市时间',
    detail_info_url  varchar(128)                       null comment '详细信息网址',
    prospectus       varchar(512)                       null comment '招股书',
    financial_report varchar(512)                       null comment '公司财报',
    category         varchar(50)                        null comment '行业分类',
    main_business    varchar(512)                       null comment '主营业务',
    user_id          bigint                             null comment '用户ID',
    create_time      datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time      datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '修改时间',
    is_delete        tinyint  default 0                 not null comment '是否删除',
    constraint t_company_uk
        unique (stock_code)
)
    comment '公司信息';


-- auto-generated definition
create table t_company_detail
(
    id                       bigint auto_increment comment 'ID'
        primary key,
    stock_code               varchar(20)                        not null comment '股票代码',
    company_name             varchar(256)                       not null comment '公司名称',
    english_name             varchar(512)                       null comment '英文名称',
    former_name              varchar(256)                       null comment '曾用名',
    territory                varchar(128)                       not null comment '所属地域',
    industry_involved        varchar(512)                       null comment '所属行业',
    company_website_address  varchar(512)                       null comment '公司网址',
    main_business            text                               null comment '主营业务',
    product_name             text                               null comment '产品名称',
    controlling_shareholder  varchar(128)                       null comment '控股股东',
    actual_controller        varchar(128)                       null comment '实际控制人',
    ultimate_controller      varchar(128)                       null comment '最终控制人',
    chairman_of_the_board    varchar(128)                       not null comment '董事长',
    secretary_to_the_board   varchar(128)                       null comment '董秘',
    corporate_representative varchar(128)                       null comment '法人代表',
    general_manager          varchar(128)                       null comment '总经理',
    registered_capital       varchar(256)                       null comment '注册资金',
    number_of_employees      int                                null comment '员工人数',
    telephone                varchar(128)                       null comment '电话',
    fax                      varchar(128)                       null comment '传真',
    postcode                 varchar(128)                       null comment '邮编',
    office_address           text                               null comment '办公地址',
    company_profile          text                               null comment '公司简介',
    create_time              datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time              datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '修改时间',
    is_delete                tinyint  default 0                 not null comment '是否删除',
    constraint t_company_detail_pk unique (stock_code)
)
    comment '公司详细信息';


-- auto-generated definition
create table t_company_detail_new
(
    id                       bigint auto_increment comment 'ID'
        primary key,
    stock_code               varchar(20)                        not null comment '股票代码',
    stock_name               varchar(50)                        not null comment '股票名称',
    company_name             varchar(256)                       not null comment '公司名称',
    english_name             varchar(512)                       null comment '英文名称',
    former_name              varchar(256)                       null comment '曾用名',
    company_profile          text                               null comment '公司简介',
    time_to_market           date                               null comment '上市时间',
    detail_info_url          varchar(128)                       null comment '公司详细信息网址',
    territory                varchar(128)                       not null comment '所属地域',
    category                 varchar(50)                        null comment '行业分类',
    company_website_address  varchar(512)                       null comment '公司网址',
    office_address           text                               null comment '办公地址',
    prospectus               varchar(512)                       null comment '招股书',
    financial_report         varchar(512)                       null comment '公司财报',
    main_business            varchar(512)                       null comment '主营业务',
    product_name             text                               null comment '产品名称',
    controlling_shareholder  varchar(128)                       null comment '控股股东',
    actual_controller        varchar(128)                       null comment '实际控制人',
    ultimate_controller      varchar(128)                       null comment '最终控制人',
    corporate_representative varchar(128)                       null comment '法人代表',
    chairman_of_the_board    varchar(128)                       null comment '董事长',
    secretary_to_the_board   varchar(128)                       null comment '董秘',
    general_manager          varchar(128)                       null comment '总经理',
    registered_capital       varchar(256)                       null comment '注册资金',
    number_of_employees      int                                null comment '员工人数',
    telephone                varchar(128)                       null comment '电话',
    fax                      varchar(128)                       null comment '传真',
    postcode                 varchar(128)                       null comment '邮编',
    campus_recruitment       varchar(512)                       null comment '校园招聘',
    social_recruitment       varchar(512)                       null comment '社会招聘',
    extend                   text                               null comment '扩展字段',
    user_id                  bigint                             null comment '用户ID',
    create_time              datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time              datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '修改时间',
    is_delete                tinyint  default 0                 not null comment '是否删除',
    constraint t_company_detail_pk
        unique (stock_code)
)
    comment '公司详细信息-新';






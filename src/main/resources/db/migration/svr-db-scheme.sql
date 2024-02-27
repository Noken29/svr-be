use svr;

create table fuel_type
(
    id          bigint auto_increment primary key,
    description varchar(48) not null default '',
    cost        double not null default 0
);

create table vehicle
(
    id                bigint auto_increment primary key,
    fuel_type_id      bigint not null,
    description       varchar(255) not null default '',
    carrying_capacity double not null default 0,
    volume            double not null default 0,
    fuel_consumption  double not null default 0,

    constraint FK_fuel_type__vehicle
    foreign key (fuel_type_id) references fuel_type (id)
);

create table routing_session
(
    id          bigint auto_increment primary key,
    last_saved  datetime(6) not null,
    description varchar(96) not null default ''
);

create table depot
(
    id                   bigint auto_increment primary key,
    routing_session_id   bigint not null,
    address_lines        varchar(255) not null default '',
    latitude             double not null default 0,
    longitude            double not null default 0,

    constraint FK_routing_session__depot
    foreign key (routing_session_id) references routing_session (id)
);

create table vehicle_routing_session
(
    id                 bigint auto_increment primary key,
    routing_session_id bigint not null,
    vehicle_id         bigint not null,

    constraint FK_vehicle__routing_session
    foreign key (vehicle_id) references vehicle (id),

    constraint FK_routing_session__vehicle
    foreign key (routing_session_id) references routing_session (id)
);

create table solution
(
    id                 bigint auto_increment primary key,
    routing_session_id bigint not null,
    created            datetime(6) not null,
    routes             json null,

    constraint FK_routing_session__solution
    foreign key (routing_session_id) references routing_session (id)
);

create table customer
(
    id                   bigint auto_increment primary key,
    routing_session_id   bigint not null,
    name                 varchar(96) not null default '',
    phone_number         varchar(48) not null default '',
    address_lines        varchar(255) not null default '',
    special_requirements text null,
    latitude             double not null default 0,
    longitude            double not null default 0,

    constraint FK_routing_session__customer
    foreign key (routing_session_id) references routing_session (id)
);

create table package
(
    id          bigint auto_increment primary key,
    customer_id bigint not null,
    type        varchar(48) not null default '',
    cost        double not null default 0,
    weight      double not null default 0,
    volume      double not null default 0,

    constraint FK_customer__package
    foreign key (customer_id) references customer (id)
);



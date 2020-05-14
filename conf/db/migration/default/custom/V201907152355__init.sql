create table movie (
    id int auto_increment primary key,
--    title varchar(256) not null unique character set utf8mb4
    title varchar(256) not null,
    screening_datetime datetime,
--    room int not null,
    unique title_screening_datetime_room(title,screening_datetime)
);

create table room_row_seat (
    id int auto_increment primary key,
    room int not null,
    `row` int not null,
    seat int not null,
    unique room_row_seat_idx(room,`row`,seat)
);

create table movie_room (
    id int auto_increment primary key,
    movie_id int,
    room_id smallint,
--    foreign key(room_id) references room_row_seat(room),
    foreign key(movie_id) references movie(id),
    unique movie_room_date_time_idx(movie_id,room_id)
);

create table ticket_price (
    id int auto_increment primary key,
--    type varchar(64) unique character set utf8mb4,
    type varchar(64) unique,
    price int,
    unique type_price_idx(type,price)
);

create table reservation (
    movie_room_id int not null,
--    schedule_id int not null,
    room_row_seat_id int not null,
--    name varchar(128) not null character set utf8mb4,
--    surname varchar(128) not null character set utf8mb4,
    name varchar(128) null,
    surname varchar(128) null,
    ticket_price_id int null,
    status varchar(64) not null default 'FREE', -- NEW, FREE, RESERVED, INVALID, PAID
    expiration_time datetime null,
    created timestamp not null default current_timestamp,
    updated timestamp not null default current_timestamp on update current_timestamp,
    primary key(movie_room_id,room_row_seat_id),
    foreign key(movie_room_id) references movie_room(id),
--    foreign key(schedule_id) references schedule(id),
    foreign key(room_row_seat_id) references room_row_seat(id),
--    foreign key(ticket_price_id) references ticket_price(id),
);
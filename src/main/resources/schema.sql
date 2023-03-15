  CREATE TABLE IF NOT EXISTS Users
  (
  id integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  name varchar (16) not null,
  email varchar (64) not null unique
  );

  CREATE TABLE IF NOT EXISTS Items
  (
  id integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  name varchar(64) not null,
  description varchar (200) not null,
  available boolean not null default true,
  user_id integer not null,
  constraint item_user_id_fk foreign key (user_id) references Users (id) on delete cascade
  );


  CREATE TABLE IF NOT EXISTS Bookings
  (
  id integer GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  start_time timestamp,
  end_time timestamp,
  status varchar (16) ,
  booker_id integer not null,
  item_id integer not null ,
  constraint booking_booker_id_fk foreign key (booker_id) REFERENCES Users (id) on delete cascade,
  constraint booking_item_id_fk foreign key (item_id) REFERENCES Items(id) on delete cascade
  );

   CREATE TABLE IF NOT EXISTS Comments
   (
   id integer GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
   text varchar (200) not null,
   author_name varchar (16) ,
   created timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
   );



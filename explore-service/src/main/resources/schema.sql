create table if not exists explore_users (
    user_id bigint GENERATED BY DEFAULT AS IDENTITY UNIQUE NOT NULL,
    email   VARCHAR(254) UNIQUE NOT NULL,
    name    VARCHAR(250) NOT NULL
);

create table if not exists categories (
    category_id bigint GENERATED BY DEFAULT AS IDENTITY UNIQUE NOT NULL,
    category_name VARCHAR(50),
    constraint uq_category_name unique (category_name)
);

create table if not exists locations (
    location_id bigint GENERATED BY DEFAULT AS IDENTITY UNIQUE NOT NULL,
    lat float not null,
    lon float not null
);

create table if not exists area (
    area_id bigint GENERATED BY DEFAULT AS IDENTITY UNIQUE NOT NULL,
    area_name VARCHAR(120) not null,
    lat float not null,
    lon float not null,
    radius integer,
    area_status VARCHAR(6)
);

create table if not exists events (
    event_id bigint GENERATED BY DEFAULT AS IDENTITY UNIQUE NOT NULL,
    created_on TIMESTAMP,
    published_on TIMESTAMP,
    title varchar(120) NOT NULL,
    annotation VARCHAR(2000) NOT NULL,
    description VARCHAR(7000) NOT NULL,
    event_date TIMESTAMP WITHOUT TIME ZONE,
    paid boolean NOT NULL,
    participants_limit integer,
    request_moderation boolean,
    category_id bigint NOT NULL,
    location_id bigint not null,
    user_id bigint not null,
    event_state varchar(10) not null,
    CONSTRAINT fk_event_category FOREIGN KEY (category_id) REFERENCES categories (category_id),
    CONSTRAINT fk_event_location FOREIGN KEY (location_id) REFERENCES locations (location_id) ON delete CASCADE,
    CONSTRAINT fk_event_initiator FOREIGN KEY (user_id) REFERENCES explore_users (user_id) ON delete CASCADE
);

create table if not exists participation_request (
    request_id bigint GENERATED BY DEFAULT AS IDENTITY UNIQUE NOT NULL,
    event_id bigint not null,
    user_id bigint not null,
    confirmed varchar(10) not null,
    created timestamp not null,
    constraint fk_event_participation foreign key (event_id) references events (event_id) on delete cascade,
    constraint fk_user_participation foreign key (user_id) references explore_users (user_id) on delete cascade
);

create table if not exists compilations (
    compilation_id bigint GENERATED BY default AS IDENTITY UNIQUE NOT NULL,
    title varchar(50) not null,
    pinned boolean not null
);

create table if not exists compilations_events (
    compilation_compilation_id bigint not null,
    events_event_id bigint not null,
    constraint fk_compilation foreign key (compilation_compilation_id) references compilations (compilation_id) on delete cascade,
    constraint fk_event foreign key (events_event_id) references events (event_id) on delete cascade
);

CREATE OR REPLACE FUNCTION distance(lat1 float, lon1 float, lat2 float, lon2 float)
    RETURNS float
AS
'
declare
    dist float = 0;
    rad_lat1 float;
    rad_lat2 float;
    theta float;
    rad_theta float;
BEGIN
    IF lat1 = lat2 AND lon1 = lon2
    THEN
        RETURN dist;
    ELSE
        rad_lat1 = pi() * lat1 / 180;
        rad_lat2 = pi() * lat2 / 180;
        theta = lon1 - lon2;
        rad_theta = pi() * theta / 180;
        dist = sin(rad_lat1) * sin(rad_lat2) + cos(rad_lat1) * cos(rad_lat2) * cos(rad_theta);

        IF dist > 1
            THEN dist = 1;
        END IF;

        dist = acos(dist);
        dist = dist * 180 / pi();
        dist = dist * 60 * 1.8524;

        RETURN dist;
    END IF;
END;
'
LANGUAGE PLPGSQL;

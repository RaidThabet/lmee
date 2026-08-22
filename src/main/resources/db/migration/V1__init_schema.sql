CREATE TABLE clubs(date_created TIMESTAMP(6) WITH TIME ZONE NOT NULL,
                   last_updated TIMESTAMP(6) WITH TIME ZONE NOT NULL,
                   id           UUID NOT NULL,
                   country      VARCHAR(255) NOT NULL,
                   name         VARCHAR(255) NOT NULL UNIQUE,
                   PRIMARY KEY (id));
CREATE TABLE clubs_matches(is_home      BOOLEAN NOT NULL,
                           date_created TIMESTAMP(6) WITH TIME ZONE NOT NULL,
                           last_updated TIMESTAMP(6) WITH TIME ZONE NOT NULL,
                           club_id      UUID NOT NULL,
                           match_id     UUID NOT NULL,
                           PRIMARY KEY (club_id, match_id));
CREATE TABLE favourite_clubs(club_id     UUID NOT NULL,
                             keycloak_id UUID NOT NULL,
                             PRIMARY KEY (club_id, keycloak_id));
CREATE TABLE matches(date_created      TIMESTAMP(6) WITH TIME ZONE NOT NULL,
                     last_updated      TIMESTAMP(6) WITH TIME ZONE NOT NULL,
                     scheduled_kickoff TIMESTAMP(6) WITH TIME ZONE,
                     id                UUID NOT NULL,
                     venue             TEXT,
                     PRIMARY KEY (id));
CREATE TABLE matches_states(away_reds    INTEGER NOT NULL,
                            away_score   INTEGER NOT NULL,
                            away_subs    INTEGER NOT NULL,
                            away_yellows INTEGER NOT NULL,
                            home_reds    INTEGER NOT NULL,
                            home_score   INTEGER NOT NULL,
                            home_subs    INTEGER NOT NULL,
                            home_yellows INTEGER NOT NULL,
                            date_created TIMESTAMP(6) WITH TIME ZONE NOT NULL,
                            last_updated TIMESTAMP(6) WITH TIME ZONE NOT NULL,
                            match_id     UUID NOT NULL,
                            status       VARCHAR(255) NOT NULL CHECK ((status IN ('SCHEDULED','IN_PROGRESS','HALF_TIME','COMPLETED','POSTPONED','ABANDONED'))),
                            PRIMARY KEY (match_id));
CREATE TABLE match_event_store(sequence_number INTEGER NOT NULL,
                                date_created    TIMESTAMP(6) WITH TIME ZONE NOT NULL,
                                last_updated    TIMESTAMP(6) WITH TIME ZONE NOT NULL,
                                occurred_at      TIMESTAMP(6) WITH TIME ZONE NOT NULL,
                                id              UUID NOT NULL,
                                match_id        UUID NOT NULL,
                                event_type      VARCHAR(255) NOT NULL CHECK ((event_type IN ('MATCH_SCHEDULED','MATCH_STARTED','FIRST_HALF_ENDED','SECOND_HALF_STARTED','FULL_TIME','MATCH_ABANDONED','MATCH_POSTPONED','GOAL_SCORED','OWN_GOAL','GOAL_CANCELED','YELLOW_CARD_GIVEN','RED_CARD_GIVEN','SECOND_YELLOW_CARD','SUBSTITUTION','PENALTY_AWARDED','PENALTY_SCORED','PENALTY_MISSED','VAR_CHECK_STARTED','VAR_DECISION','ADDED_TIME_ANNOUNCED'))),
                                payload         JSONB NOT NULL,
                                PRIMARY KEY (id));
CREATE TABLE players(date_created TIMESTAMP(6) WITH TIME ZONE NOT NULL,
                     last_updated TIMESTAMP(6) WITH TIME ZONE NOT NULL,
                     club_id      UUID NOT NULL,
                     id           UUID NOT NULL,
                     name         VARCHAR(255) NOT NULL,
                     PRIMARY KEY (id));
CREATE TABLE users(date_created TIMESTAMP(6) WITH TIME ZONE NOT NULL,
                   last_updated TIMESTAMP(6) WITH TIME ZONE NOT NULL,
                   keycloak_id  UUID NOT NULL,
                   PRIMARY KEY (keycloak_id));
ALTER TABLE IF EXISTS clubs_matches
    ADD CONSTRAINT FKhu301r41x3jhshyrykc4ltvkx FOREIGN KEY (club_id) REFERENCES clubs;
ALTER TABLE IF EXISTS clubs_matches
    ADD CONSTRAINT FKb3w9etbpgn0mcchbcyvrixr2c FOREIGN KEY (match_id) REFERENCES matches;
ALTER TABLE IF EXISTS favourite_clubs
    ADD CONSTRAINT FKnk86ekdry2wni5oo14ya8mwc8 FOREIGN KEY (club_id) REFERENCES clubs;
ALTER TABLE IF EXISTS favourite_clubs
    ADD CONSTRAINT FK2y1j2k09pbur7718kh4t0byol FOREIGN KEY (keycloak_id) REFERENCES users;
ALTER TABLE IF EXISTS matches_states
    ADD CONSTRAINT FKrwrsepg8s3bvpcngen21fpeqn FOREIGN KEY (match_id) REFERENCES matches;
ALTER TABLE IF EXISTS match_event_store
    ADD CONSTRAINT FK70o9pkpka3cna433hkd2yalcc FOREIGN KEY (match_id) REFERENCES matches;
ALTER TABLE IF EXISTS players
    ADD CONSTRAINT FK9wl3ooravcu9li0ppu50rft6h FOREIGN KEY (club_id) REFERENCES clubs;
ALTER TABLE IF EXISTS match_event_store ADD CONSTRAINT event_sequence_number_unique_per_match
    UNIQUE (match_id, sequence_number);
ALTER TABLE clubs_matches ADD CONSTRAINT uq_match_side
    UNIQUE (match_id, is_home);
CREATE INDEX idx_players_club ON players (club_id);
CREATE INDEX idx_clubs_matches_match ON clubs_matches (match_id);
CREATE INDEX idx_fav_keycloak ON favourite_clubs (keycloak_id);
CREATE INDEX idx_matches_kickoff ON matches (scheduled_kickoff);
drop table if exists TECNICOS;
drop table if exists TERMO_FECHO;
drop table if exists TERMO_ABERTURA;
drop table if exists DOCUMENTOS;
drop table if exists IMAGENS;
drop table if exists REGISTO;
drop table if exists MEMBROS;
drop table if exists OBRA;
drop table if exists SESSAO;
drop table if exists UTILIZADOR;
drop table if exists LOCALIDADES;

create table UTILIZADOR (
                            id serial,
                            img_pfp bytea,
                            email varchar(255) unique,
                            role varchar(8),
                            username varchar(255) unique,
                            password varchar(255),
                            nome varchar(50),
                            apelido varchar(50),
                            telefone varchar(9),
                            freguesia varchar(50),
                            concelho varchar(50),
                            distrito varchar(50),
                            primary key (id),
                            constraint Role_Format check (ROLE IN ('OPERÁRIO', 'CÂMARA')),
                            constraint Email_Format check (email like '%@%.%')
                            --constraint Telefone_Format check (telefone like '^[0-9]+$')
);

create table SESSAO (
                        token_validation varchar(256),
                        uId int,
                        created_at bigint not null,
                        last_used_at bigint not null,
                        primary key (uId, token_validation),
                        constraint UserId foreign key (uId) references UTILIZADOR(id)
);

create table OBRA (
                      id varchar(255),
                      featuredImg bytea,
                      nome varchar(50),
                      tipo varchar(50),
                      descricao varchar(500),
                      estado varchar(50),
                      freguesia varchar(50),
                      concelho varchar(50),
                      distrito varchar(50),
                      rua varchar(255),
                      cPostal varchar(8),
                      primary key (id),
                      constraint cPostal_format check (cPostal LIKE '%-%'),
                      constraint Tipo CHECK (tipo IN ('RESIDENCIAL','COMERCIAL','INDUSTRIAL','INFRAESTRUTURA','INSTITUCIONAL','REABILITAÇÃO','ESTRUTURA ESPECIAL','OBRA DE ARTE','HABITAÇÃO','EDIFICIOS ESPECIAL'))
);

create table MEMBROS (
                          uId integer,
                          oId varchar(50),
                          role varchar(255),
                          primary key (uId, oId),
                          constraint UserId foreign key (uId) references UTILIZADOR(id),
                          constraint ObraId foreign key (oId) references OBRA(id)
);

create table REGISTO (
                         id serial,
                         oId varchar(50),
                         texto varchar(2500),
                         estado varchar(50),
                         creation_date timestamp,
                         last_modification_date timestamp,
                         author integer,
                         primary key (id, oId),
                         constraint ObraId foreign key (oId) references OBRA(id),
                         constraint UserId foreign key (author) references UTILIZADOR(id)
);

create table IMAGENS (
                         id serial,
                         oId varchar(50),
                         rId integer,
                         name varchar(255),
                         upload_date date,
                         imagem bytea,
                         primary key (id, oId, rId),
                         constraint RegistoId foreign key (rId, oId) references REGISTO(id, oId)
);

create table DOCUMENTOS (
                            id serial,
                            oId varchar(50),
                            rId integer,
                            name varchar(255),
                            upload_date date,
                            file bytea,
                            primary key (id, oId, rId),
                            constraint RegistoId foreign key (rId, oId) references REGISTO(id, oId)
);

create table TERMO_ABERTURA (
                                id serial,
                                oId varchar(50),
                                inicio timestamp,
                                camara varchar(255),
                                titular_licença varchar(255),
                                construção varchar(255),
                                diretor varchar(255),
                                predio varchar(255),
                                primary key (id, oId),
                                constraint ObraId foreign key (oId) references OBRA(id)

);

create table TECNICOS (
                         tId integer,
                         oId varchar(50),
                         id integer,
                         nome varchar(50),
                         tipo varchar(50),
                         associacao varchar(255),
                         numero integer,
                         primary key (id, tId, oId),
                         constraint ObraId foreign key (oId) references OBRA(id),
                         constraint TermoId foreign key (tId, oId) references TERMO_ABERTURA(id, oId)
);

create table TERMO_FECHO (
                             id serial,
                             oId varchar(50),
                             procedimento varchar(255),
                             numero integer,
                             inicio timestamp,
                             conclusao timestamp,
                             abertura integer,
                             dono varchar(50),
                             diretor varchar(50),
                             primary key (id, oId),
                             constraint ObraId foreign key (oId) references OBRA(id),
                             constraint TermoAberturaId foreign key (abertura, oId) references TERMO_ABERTURA(id, oId)
);

create table LOCALIDADES (
                            id serial,
                            freguesia varchar(255),
                            concelho varchar (255),
                            distrito varchar (50)
);
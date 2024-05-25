drop table if exists TECNICO;
drop table if exists TERMO_FECHO;
drop table if exists TERMO_ABERTURA;
drop table if exists EMPRESA_CONSTRUCAO;
drop table if exists CAMARA_MUNICIPAL;
drop table if exists DOCUMENTO;
drop table if exists IMAGEM;
drop table if exists REGISTO;
drop table if exists MEMBRO;
drop table if exists OBRA;
drop table if exists SESSAO;
drop table if exists profile_picture;
drop table if exists UTILIZADOR;
--drop table if exists LOCALIDADE;

create table UTILIZADOR
(
    id        serial,
    img_pfp   bytea,
    email     varchar(255) unique,
    role      varchar(8),
    username  varchar(255) unique,
    password  varchar(255),
    nome      varchar(50),
    apelido   varchar(50),
    nif       integer unique,
    telefone  varchar(9),
    freguesia varchar(255),
    concelho  varchar(255),
    distrito  varchar(255),
    primary key (id),
    constraint Role_Format check (ROLE IN ('OPERÁRIO', 'CÂMARA')), -- Adicionar ADMIN?
    constraint Email_Format check (email like '%@%.%')
    --constraint Telefone_Format check (telefone not like '%[^0-9]%') -- This may work
);

create table SESSAO
(
    token_validation varchar(256),
    uId              int,
    created_at       bigint not null,
    last_used_at     bigint not null,
    primary key (uId, token_validation),
    constraint UserId foreign key (uId) references UTILIZADOR (id)
);

create table OBRA
(
    id          varchar(255),
    featuredImg bytea,
    nome        varchar(50),
    tipo        varchar(50),
    descricao   varchar(500),
    estado      varchar(50),
    freguesia   varchar(255),
    concelho    varchar(255),
    distrito    varchar(255),
    rua         varchar(255),
    cPostal     varchar(8),
    primary key (id),
    constraint cPostal_format check (cPostal LIKE '%-%'),
    constraint Tipo CHECK (tipo IN
                           ('RESIDENCIAL', 'COMERCIAL', 'INDUSTRIAL', 'INFRAESTRUTURA', 'INSTITUCIONAL', 'REABILITAÇÃO',
                            'ESTRUTURA ESPECIAL', 'OBRA DE ARTE', 'HABITAÇÃO', 'EDIFICIOS ESPECIAL')),
    constraint Estado CHECK (estado IN ('EM PROGRESSO', 'TERMINADA', 'CANCELADA', 'EM PAUSA'))
);

create table MEMBRO
(
    uId  integer,
    oId  varchar(255),
    role varchar(255),
    primary key (uId, oId),
    constraint UserId foreign key (uId) references UTILIZADOR (id),
    constraint ObraId foreign key (oId) references OBRA (id),
    constraint Role CHECK (role IN ('ADMIN', 'MEMBRO', 'CAMARA', 'TECNICO'))
);

create table REGISTO
(
    id                     serial,
    oId                    varchar(255),
    titulo                 varchar(255),
    texto                  varchar(2500),
    estado                 varchar(50),
    creation_date          timestamp,
    last_modification_date timestamp,
    author                 integer,
    primary key (id, oId),
    constraint ObraId foreign key (oId) references OBRA (id),
    constraint UserId foreign key (author) references UTILIZADOR (id),
    constraint Estado CHECK (estado IN ('EDITÁVEL', 'FINISHED'))
);

create table IMAGEM
(
    id                 serial,
    oId                varchar(255),
    rId                integer,
    name               varchar(255),
    upload_date        date,
    content            bytea,
    compressed_content bytea,
    primary key (id, oId, rId),
    constraint RegistoId foreign key (rId, oId) references REGISTO (id, oId)
);

create table DOCUMENTO
(
    id          serial,
    oId         varchar(255),
    rId         integer,
    name        varchar(255),
    upload_date date,
    file        bytea,
    primary key (id, oId, rId),
    constraint RegistoId foreign key (rId, oId) references REGISTO (id, oId)
);

create table EMPRESA_CONSTRUCAO
(
    id     serial,
    nome   varchar(255),
    numero integer,
    primary key (id)
);

--create table LOCALIDADE
--(
--    id        serial,
--    distrito  varchar(255),
--    concelho  varchar(255),
--    freguesia varchar(255),
--    primary key (id)
--);

create table TERMO_ABERTURA
(
    id                 serial,
    oId                varchar(255),
    inicio             timestamp,
    camara             integer,
    titular_licenca    varchar(255),
    empresa_construcao integer,
    predio             varchar(255),
    primary key (id, oId),
    constraint ObraId foreign key (oId) references OBRA (id),
    constraint EmpresaId foreign key (empresa_construcao) references EMPRESA_CONSTRUCAO (id),
    constraint CamaraId foreign key (camara) references localidade (id)
);

create table TECNICO
(
    nif        integer,
    tId        integer,
    oId        varchar(255),
    nome       varchar(50),
    tipo       varchar(50),
    associacao varchar(255),
    numero     integer,
    primary key (nif, tId, oId),
    constraint ObraId foreign key (oId) references OBRA (id),
    constraint TermoId foreign key (tId, oId) references TERMO_ABERTURA (id, oId),
    constraint Tipo CHECK (tipo IN ('FISCALIZAÇÃO', 'COORDENADOR', 'ARQUITETURA', 'ESTABILIDADE', 'ELETRICIDADE', 'GÁS',
                                    'CANALIZAÇÃO', 'TELECOMUNICAÇÕES', 'TERMICO', 'ACUSTICO', 'TRANSPORTES', 'DIRETOR'))
);

create table TERMO_FECHO
(
    id           serial,
    oId          varchar(255),
    --procedimento varchar(255),
    --numero       integer,
    inicio       timestamp,
    conclusao    timestamp,
    abertura     integer,
    fiscalização varchar(50),
    diretor      varchar(50),
    primary key (id, oId),
    constraint ObraId foreign key (oId) references OBRA (id),
    constraint TermoAberturaId foreign key (abertura, oId) references TERMO_ABERTURA (id, oId)
);

create table profile_picture
(
    id      serial,
    user_id integer,
    name    varchar(255),
    type    varchar(255),
    img     bytea,
    primary key (id),
    constraint UserId foreign key (user_id) references UTILIZADOR (id)
);
drop table if exists TERMO_FECHO;
drop table if exists TERMO_ABERTURA;
drop table if exists EMPRESA_CONSTRUCAO;
drop table if exists CAMARA_MUNICIPAL;
drop table if exists DOCUMENTO;
drop table if exists IMAGEM;
drop table if exists REGISTO;
drop table if exists MEMBRO;
drop table if exists CONVITE;
drop table if exists IMAGEM_OBRA;
drop table if exists OBRA;
drop table if exists SESSAO;
drop table if exists PROFILE_PICTURE;
drop table if exists PENDENTE;
drop table if exists UTILIZADOR;
--drop table if exists LOCALIDADE;

create table UTILIZADOR
(
    id                serial,
    email             varchar(255) unique,
    username          varchar(255) unique,
    nif               integer unique,
    role              varchar(8),
    password          varchar(255),
    nome              varchar(50),
    apelido           varchar(50),
    associacao_nome   varchar(255),
    associacao_numero integer,
    telefone          varchar(9),
    freguesia         varchar(255),
    concelho          varchar(255),
    distrito          varchar(255),
    primary key (id),
    constraint Role_Format check (ROLE IN ('OPERÁRIO', 'CÂMARA', 'ADMIN')),
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
    id        varchar(255),
    nome      varchar(50),
    tipo      varchar(50),
    descricao varchar(500),
    estado    varchar(50),
    freguesia varchar(255),
    concelho  varchar(255),
    distrito  varchar(255),
    rua       varchar(255),
    cPostal   varchar(8),
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
    constraint MEMBER_ROLE CHECK (role IN ('ADMIN', 'MEMBRO', 'ESPECTADOR', 'FISCALIZAÇÃO', 'COORDENADOR',
                                           'ARQUITETURA', 'ESTABILIDADE', 'ELETRICIDADE', 'GÁS', 'CANALIZAÇÃO',
                                           'TELECOMUNICAÇÕES', 'TERMICO', 'ACUSTICO',
                                           'TRANSPORTES', 'DIRETOR'))
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
    constraint Estado CHECK (estado IN ('EDITÁVEL', 'NÃO EDITÁVEL'))
);

create table IMAGEM
(
    id          serial,
    oId         varchar(255),
    rId         integer,
    name        varchar(255),
    type        varchar(255),
    file        bytea,
    upload_date date,
    primary key (id, oId, rId),
    constraint ObraId foreign key (oId) references OBRA (id),
    constraint RegistoId foreign key (rId, oId) references REGISTO (id, oId)
);

create table DOCUMENTO
(
    id          serial,
    oId         varchar(255),
    rId         integer,
    name        varchar(255),
    type        varchar(255),
    file        bytea,
    upload_date date,
    primary key (id, oId, rId),
    constraint ObraId foreign key (oId) references OBRA (id),
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

create table TERMO_FECHO
(
    id             serial,
    oId            varchar(255),
    data_conclusao timestamp,
    abertura       integer,
    fiscalização   integer,
    diretor        integer,
    primary key (id, oId),
    constraint ObraId foreign key (oId) references OBRA (id),
    constraint TermoAberturaId foreign key (abertura, oId) references TERMO_ABERTURA (id, oId),
    constraint FiscalizacaoId foreign key (fiscalização, oId) references MEMBRO (uId, oId),
    constraint DiretorId foreign key (diretor, oId) references MEMBRO (uId, oId)
);

create table PROFILE_PICTURE
(
    id      serial,
    user_id integer,
    name    varchar(255),
    type    varchar(255),
    file     bytea,
    primary key (id),
    constraint UserId foreign key (user_id) references UTILIZADOR (id)
);

create table IMAGEM_OBRA
(
    id      serial,
    work_id varchar(255),
    name    varchar(255),
    type    varchar(255),
    file     bytea,
    primary key (id),
    constraint WorkId foreign key (work_id) references OBRA (id)
);

create table CONVITE
(
    id    varchar(255),
    email varchar(255),
    role  varchar(50),
    oId   varchar(255),
    primary key (id, oId),
    constraint ObraId foreign key (oId) references OBRA (id),
    constraint Tipo CHECK (role IN ('MEMBRO', 'VIEWER', 'FISCALIZAÇÃO', 'COORDENADOR', 'ARQUITETURA', 'ESTABILIDADE',
                                    'ELETRICIDADE', 'GÁS',
                                    'CANALIZAÇÃO', 'TELECOMUNICAÇÕES', 'TERMICO', 'ACUSTICO', 'TRANSPORTES',
                                    'DIRETOR')),
    constraint Email_Format check (email like '%@%.%')
);

create table PENDENTE
(
    id  serial,
    uId integer,
    primary key (id),
    constraint UserId foreign key (uId) references UTILIZADOR (id)
)
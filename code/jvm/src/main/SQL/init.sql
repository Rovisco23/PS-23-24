delete from TERMO_ABERTURA;
delete from DOCUMENTO;
delete from IMAGEM;
delete from REGISTO;
delete from MEMBRO;
delete from OBRA;
delete from SESSAO;
delete from UTILIZADOR;
delete from LOCALIDADE;

insert into utilizador(email, role, username, password, nome, apelido, nif, telefone, freguesia, concelho, distrito,
                       associacao_nome, associacao_numero, pendente)
values ('a49508@alunos.isel.pt', 'ADMIN', 'JMota15', '12345', 'João', 'Mota', 266573150, '910937960',
        'União das freguesias de Charneca de Caparica e Sobreda', 'Almada', 'Setúbal', 'Site Diary Team', 1, false),
       ('ricardorovisco23@gmail.com', 'ADMIN', 'Rovisco23', '12345', 'Ricardo', 'Rovisco', 234565432, NULL,
        'Santa Clara', 'Lisboa', 'Lisboa', 'Site Diary Team', 2, false);

-- Alterar local do ficheiro conforme necessário
COPY LOCALIDADE (distrito, concelho, freguesia)
    FROM 'E:\Documentos\Universidade\Projeto\PS-23-24\docs\localidades.csv' DELIMITER ';' CSV HEADER;


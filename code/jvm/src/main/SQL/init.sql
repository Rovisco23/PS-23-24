delete from TERMO_ABERTURA;
delete from DOCUMENTO;
delete from IMAGEM;
delete from REGISTO;
delete from MEMBRO;
delete from OBRA;
delete from SESSAO;
delete from UTILIZADOR;
delete from LOCALIDADE;

insert into utilizador(email, role, username, password, nome, apelido, nif, telefone, freguesia, concelho, distrito, associacao_nome, associacao_numero, pendente) values
    ('ricardorovisco23@gmail.com', 'ADMIN', 'Rovisco23', '12345', 'Ricardo', 'Rovisco', 234565432, NULL, 'Santa Clara', 'Lisboa', 'Lisboa', 'Site Diary Team', 1, false),
    ('a49508@alunos.isel.pt', 'ADMIN', 'JMota15', '12345', 'João', 'Mota', 266573150, '910937960', 'União das freguesias de Charneca de Caparica e Sobreda', 'Almada', 'Setúbal', 'Site Diary Team', 2, false);

-- Alterar local do ficheiro conforme necessário
COPY LOCALIDADE (distrito, concelho, freguesia)
    FROM 'E:\Documentos\Universidade\Projeto\PS-23-24\docs\localidades.csv' DELIMITER ';' CSV HEADER;

insert into membro(oid, uid, role) values ('90e825a3-b088-4260-b3cf-44191759bcd6', 2, 'DIRETOR');

insert into registo(oid, titulo,texto, estado, creation_date, last_modification_date, author)
values ('423413b1-a8de-420c-8f5d-de3cb30dbed6','O andaime não se encontrava corretamente instalado','No dia x observei que o andaime estava mal montado','FINISHED',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,1),
       ('423413b1-a8de-420c-8f5d-de3cb30dbed6','Um dos tubos comprados encontra-se com defeito','Foi necessário comprar novos tubos aumentando a conta da obra por 50€','EDITÁVEL',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,1 );

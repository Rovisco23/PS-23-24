delete from TERMO_FECHO;
delete from TERMO_ABERTURA;
delete from DOCUMENTO;
delete from IMAGEM;
delete from REGISTO;
delete from MEMBRO;
delete from OBRA;
delete from SESSAO;
delete from UTILIZADOR;
delete from LOCALIDADE;

insert into utilizador(email, role, username, password, nome, apelido, nif, telefone, freguesia, concelho, distrito, associacao_nome, associacao_numero) values
    ('ricardorovisco23@gmail.com', 'ADMIN', 'Rovisco23', '12345', 'Ricardo', 'Rovisco', 234565432, NULL, 'Santa Clara', 'Lisboa', 'Lisboa', 'Site Diary Team', 1),
    ('joaomota15@gmail.com', 'ADMIN', 'JMota15', '12345', 'João', 'Mota', 123454332, '917709988', 'Margem', 'Sul', 'Nárnia', 'Site Diary Team', 2);

-- Alterar local do ficheiro conforme necessário
COPY LOCALIDADE (distrito, concelho, freguesia)
    FROM 'E:\Documentos\Universidade\Projeto\PS-23-24\docs\localidades.csv' DELIMITER ';' CSV HEADER;

insert into membro(oid, uid, role) values ('90e825a3-b088-4260-b3cf-44191759bcd6', 2, 'DIRETOR');

insert into registo(oid, titulo,texto, estado, creation_date, last_modification_date, author)
    values ('90e825a3-b088-4260-b3cf-44191759bcd6','O andaime não se encontrava corretamente instalado','No dia x observei que o andaime estava mal montado','FINISHED',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,1),
           ('90e825a3-b088-4260-b3cf-44191759bcd6','Um dos tubos comprados encontra-se com defeito','Foi necessário comprar novos tubos aumentando a conta da obra por 50€','EDITÁVEL',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,1 );

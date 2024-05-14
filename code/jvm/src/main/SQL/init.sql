delete from LOCALIDADE;
delete from TECNICO;
delete from TERMO_FECHO;
delete from TERMO_ABERTURA;
delete from DOCUMENTO;
delete from IMAGEM;
delete from REGISTO;
delete from MEMBRO;
delete from OBRA;
delete from SESSAO;
delete from UTILIZADOR;

insert into utilizador(email, role, username, password, nome, apelido, nif, telefone, freguesia, concelho, distrito) values
    ('ricardorovisco23@gmail.com', 'OPERÁRIO', 'Rovisco23', '12345', 'Ricardo', 'Rovisco', 234565432, NULL, 'Santa Clara', 'Lisboa', 'Lisboa'),
    ('joaomota15@gmail.com', 'CÂMARA', 'JMota15', '12345', 'João', 'Mota', 123454332, '917709988', 'Margem', 'Sul', 'Nárnia');

-- Alterar local do ficheiro conforme necessário
COPY LOCALIDADE (distrito, concelho, freguesia)
    FROM 'E:\Documentos\Universidade\Projeto\PS-23-24\docs\localidades.csv' DELIMITER ';' CSV HEADER;
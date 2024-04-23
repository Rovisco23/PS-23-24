delete from TECNICOS;
delete from TERMO_FECHO;
delete from TERMO_ABERTURA;
delete from DOCUMENTOS;
delete from IMAGENS;
delete from REGISTO;
delete from MEMBROS;
delete from OBRA;
delete from SESSAO;
delete from UTILIZADOR;

insert into utilizador(email, role, username, password, nome, apelido, telefone, freguesia, concelho, distrito) values
    ('ricardorovisco23@gmail.com', 'OPERÁRIO', 'Rovisco23', '12345', 'Ricardo', 'Rovisco', NULL, 'Santa Clara', 'Lisboa', 'Lisboa'),
    ('joaomota15@gmail.com', 'CÂMARA', 'JMota15', '12345', 'João', 'Mota', '917709988', 'Margem', 'Sul', 'Nárnia')


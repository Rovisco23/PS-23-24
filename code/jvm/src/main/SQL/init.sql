--delete from LOCALIDADE;
--delete from TECNICO;
--delete from TERMO_FECHO;
--delete from TERMO_ABERTURA;
--delete from DOCUMENTO;
--delete from IMAGEM;
--delete from REGISTO;
--delete from MEMBRO;
--delete from OBRA;
--delete from SESSAO;
--delete from UTILIZADOR;

insert into utilizador(email, role, username, password, nome, apelido, telefone, freguesia, concelho, distrito) values
    ('ricardorovisco23@gmail.com', 'OPERÁRIO', 'Rovisco23', '12345', 'Ricardo', 'Rovisco', NULL, 'Santa Clara', 'Lisboa', 'Lisboa'),
    ('joaomota15@gmail.com', 'CÂMARA', 'JMota15', '12345', 'João', 'Mota', '917709988', 'Margem', 'Sul', 'Nárnia');

-- Alterar local do ficheiro conforme necessário
COPY LOCALIDADE (distrito, concelho, freguesia)
    FROM 'C:\Users\voyst\OneDrive\Ambiente de Trabalho\things\escola\2 Semestre\Projeto\Site Diary\docs\localidades2.csv' DELIMITER ';' CSV HEADER;
-- ENDEREÇOS
INSERT INTO tb_endereco (rua, numero, complemento, bairro, cidade, estado, cep) VALUES ('Rua Principal', '100', 'Apto 101', 'Centro', 'São Paulo', 'SP', '01000-000');
INSERT INTO tb_endereco (rua, numero, complemento, bairro, cidade, estado, cep) VALUES ('Rua A', '123', 'Casa 1', 'Jardim das Flores', 'Rio de Janeiro', 'RJ', '20000-000');
INSERT INTO tb_endereco (rua, numero, complemento, bairro, cidade, estado, cep) VALUES ('Avenida B', '456', '', 'Centro', 'Belo Horizonte', 'MG', '30000-000');
INSERT INTO tb_endereco (rua, numero, complemento, bairro, cidade, estado, cep) VALUES ('Rua C', '789', 'Casa 2', 'Vila Nova', 'Curitiba', 'PR', '40000-000');
INSERT INTO tb_endereco (rua, numero, complemento, bairro, cidade, estado, cep) VALUES ('Travessa D', '321', '', 'São João', 'Porto Alegre', 'RS', '50000-000');
INSERT INTO tb_endereco (rua, numero, complemento, bairro, cidade, estado, cep) VALUES ('Praça E', '654', 'Loja 3', 'Shopping Center', 'Fortaleza', 'CE', '60000-000');
INSERT INTO tb_endereco (rua, numero, complemento, bairro, cidade, estado, cep) VALUES ('Rua F', '987', 'Apto 202', 'Vila Verde', 'Salvador', 'BA', '70000-000');
INSERT INTO tb_endereco (rua, numero, complemento, bairro, cidade, estado, cep) VALUES ('Avenida G', '147', '', 'Jardim das Palmeiras', 'Recife', 'PE', '80000-000');
INSERT INTO tb_endereco (rua, numero, complemento, bairro, cidade, estado, cep) VALUES ('Rua H', '258', 'Sala 101', 'Vila Nova', 'Manaus', 'AM', '90000-000');
INSERT INTO tb_endereco (rua, numero, complemento, bairro, cidade, estado, cep) VALUES ('Alameda I', '369', '', 'Parque Industrial', 'São Luís', 'MA', '92000-000');


-- USUÁRIOS (Clientes e Administradores)
INSERT INTO tb_usuario (nome, email, senha, endereco_id, telefone, role, cpf) VALUES ('Admin', 'admin@email.com', 'admin123', 1, '11999999999', 'ADMIN', '12345678900');
INSERT INTO tb_usuario (nome, email, senha, endereco_id, telefone, role, cpf) VALUES ('João Silva', 'joao@email.com', '123456', 2, '11987654321', 'CLIENTE', '98765432100');
INSERT INTO tb_usuario (nome, email, senha, endereco_id, telefone, role, cpf) VALUES ('Maria Souza', 'maria@email.com', '654321', 3, '11876543210', 'CLIENTE', '98765432101');
INSERT INTO tb_usuario (nome, email, senha, endereco_id, telefone, role, cpf) VALUES ('Carlos Mendes', 'carlos@email.com', 'car123', 4, '11765432109', 'CLIENTE', '98765432102');
INSERT INTO tb_usuario (nome, email, senha, endereco_id, telefone, role, cpf) VALUES ('Ana Pereira', 'ana@email.com', 'ana456', 5, '11654321098', 'CLIENTE', '98765432103');
INSERT INTO tb_usuario (nome, email, senha, endereco_id, telefone, role, cpf) VALUES ('Paulo Henrique', 'paulo@email.com', 'paulo789', 6, '11543210987', 'CLIENTE', '98765432104');
INSERT INTO tb_usuario (nome, email, senha, endereco_id, telefone, role, cpf) VALUES ('Fernanda Lima', 'fernanda@email.com', 'fernanda123', 7, '11432109876', 'CLIENTE', '98765432105');
INSERT INTO tb_usuario (nome, email, senha, endereco_id, telefone, role, cpf) VALUES ('Ricardo Alves', 'ricardo@email.com', 'ricardo456', 8, '11321098765', 'CLIENTE', '98765432106');
INSERT INTO tb_usuario (nome, email, senha, endereco_id, telefone, role, cpf) VALUES ('Juliana Castro', 'juliana@email.com', 'juliana789', 9, '11210987654', 'CLIENTE', '98765432107');
INSERT INTO tb_usuario (nome, email, senha, endereco_id, telefone, role, cpf) VALUES ('Gabriel Martins', 'gabriel@email.com', 'gabriel987', 10, '11109876543', 'CLIENTE', '98765432108');


-- PRODUTOS
INSERT INTO tb_produto (nome, descricao, preco, quantidade_estoque, categoria, imagem) VALUES ('Arroz', 'Pacote de 5kg', 25.90, 50, 'Alimentos', 'img1.jpg');
INSERT INTO tb_produto (nome, descricao, preco, quantidade_estoque, categoria, imagem) VALUES ('Feijão', 'Pacote de 1kg', 8.90, 40, 'Alimentos', 'img2.jpg');
INSERT INTO tb_produto (nome, descricao, preco, quantidade_estoque, categoria, imagem) VALUES ('Óleo de Soja', 'Garrafa de 900ml', 7.50, 30, 'Alimentos', 'img3.jpg');
INSERT INTO tb_produto (nome, descricao, preco, quantidade_estoque, categoria, imagem) VALUES ('Leite', 'Caixa 1L', 4.80, 60, 'Laticínios', 'img4.jpg');
INSERT INTO tb_produto (nome, descricao, preco, quantidade_estoque, categoria, imagem) VALUES ('Pão de Forma', 'Pão 500g', 6.20, 20, 'Padaria', 'img5.jpg');
INSERT INTO tb_produto (nome, descricao, preco, quantidade_estoque, categoria, imagem) VALUES ('Detergente', '500ml', 2.30, 100, 'Limpeza', 'img6.jpg');
INSERT INTO tb_produto (nome, descricao, preco, quantidade_estoque, categoria, imagem) VALUES ('Sabão em Pó', '1kg', 10.50, 35, 'Limpeza', 'img7.jpg');
INSERT INTO tb_produto (nome, descricao, preco, quantidade_estoque, categoria, imagem) VALUES ('Refrigerante', 'Garrafa 2L', 9.00, 25, 'Bebidas', 'img8.jpg');
INSERT INTO tb_produto (nome, descricao, preco, quantidade_estoque, categoria, imagem) VALUES ('Cerveja', 'Lata 350ml', 3.50, 80, 'Bebidas', 'img9.jpg');
INSERT INTO tb_produto (nome, descricao, preco, quantidade_estoque, categoria, imagem) VALUES ('Chocolate', 'Barra 90g', 5.80, 45, 'Doces', 'img10.jpg');

-- PEDIDOS
INSERT INTO tb_pedido (usuario_id, data_criacao, status) VALUES (2, '2024-02-02 10:00:00', 'PENDENTE');
INSERT INTO tb_pedido (usuario_id, data_criacao, status) VALUES (3, '2024-02-01 14:30:00', 'CONCLUIDO');
INSERT INTO tb_pedido (usuario_id, data_criacao, status) VALUES (4, '2024-01-30 18:20:00', 'CANCELADO');
INSERT INTO tb_pedido (usuario_id, data_criacao, status) VALUES (5, '2024-01-28 09:15:00', 'EM_SEPARACAO');
INSERT INTO tb_pedido (usuario_id, data_criacao, status) VALUES (6, '2024-01-27 20:10:00', 'EM_ENTREGA');
INSERT INTO tb_pedido (usuario_id, data_criacao, status) VALUES (7, '2024-01-26 15:45:00', 'PENDENTE');
INSERT INTO tb_pedido (usuario_id, data_criacao, status) VALUES (8, '2024-01-25 13:50:00', 'CONCLUIDO');
INSERT INTO tb_pedido (usuario_id, data_criacao, status) VALUES (9, '2024-01-24 11:05:00', 'EM_SEPARACAO');
INSERT INTO tb_pedido (usuario_id, data_criacao, status) VALUES (10, '2024-01-23 08:30:00', 'EM_ENTREGA');
INSERT INTO tb_pedido (usuario_id, data_criacao, status) VALUES (2, '2024-01-22 17:25:00', 'CANCELADO');

-- ITENS DOS PEDIDOS
INSERT INTO tb_item_pedido (pedido_id, produto_id, quantidade, preco_unitario) VALUES (1, 1, 2, 25.90);
INSERT INTO tb_item_pedido (pedido_id, produto_id, quantidade, preco_unitario) VALUES (1, 5, 1, 6.20);
INSERT INTO tb_item_pedido (pedido_id, produto_id, quantidade, preco_unitario) VALUES (2, 2, 3, 8.90);
INSERT INTO tb_item_pedido (pedido_id, produto_id, quantidade, preco_unitario) VALUES (2, 8, 1, 9.00);
INSERT INTO tb_item_pedido (pedido_id, produto_id, quantidade, preco_unitario) VALUES (3, 3, 4, 7.50);
INSERT INTO tb_item_pedido (pedido_id, produto_id, quantidade, preco_unitario) VALUES (4, 4, 2, 4.80);
INSERT INTO tb_item_pedido (pedido_id, produto_id, quantidade, preco_unitario) VALUES (5, 6, 5, 2.30);
INSERT INTO tb_item_pedido (pedido_id, produto_id, quantidade, preco_unitario) VALUES (6, 7, 2, 10.50);
INSERT INTO tb_item_pedido (pedido_id, produto_id, quantidade, preco_unitario) VALUES (7, 9, 6, 3.50);
INSERT INTO tb_item_pedido (pedido_id, produto_id, quantidade, preco_unitario) VALUES (8, 10, 3, 5.80);
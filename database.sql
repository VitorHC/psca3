DROP DATABASE IF EXISTS clinica;
CREATE DATABASE clinica;
USE clinica;

CREATE TABLE pessoas(
  `id` INT NOT NULL AUTO_INCREMENT,
  `nome` VARCHAR(255) NOT NULL,
  `endereco` VARCHAR(255) NOT NULL,
  `email` VARCHAR(255) NOT NULL,
  `dataDeNascimento` DATE NOT NULL,
  `telefone` VARCHAR(45) NULL,
  `celular` VARCHAR(45) NULL,
  PRIMARY KEY (`id`),
  UNIQUE (`email`)
) ENGINE=INNODB;

CREATE TABLE funcionarios(
  `pessoaId` INT NOT NULL,
  `senha` VARCHAR(45) NOT NULL,
  UNIQUE (`pessoaId`),

  FOREIGN KEY (`pessoaId`)
  REFERENCES `pessoas` (`id`)
  ON DELETE CASCADE
) ENGINE=INNODB;

CREATE TABLE pacientes(
  `pessoaId` INT NOT NULL,
  `cpf` VARCHAR(45) NOT NULL,
  UNIQUE (`cpf`),
  UNIQUE (`pessoaId`),

  FOREIGN KEY (`pessoaId`)
  REFERENCES `pessoas` (`id`)
  ON DELETE CASCADE
) ENGINE=INNODB;

CREATE TABLE medicos(
  `pessoaId` INT NOT NULL,
  `crm` VARCHAR(45) NOT NULL,
  `especialidade` VARCHAR(255) NOT NULL,
  UNIQUE (`crm`),
  UNIQUE (`pessoaId`),

  FOREIGN KEY (`pessoaId`)
  REFERENCES `pessoas` (`id`)
  ON DELETE CASCADE
) ENGINE=INNODB;

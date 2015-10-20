CREATE DATABASE  IF NOT EXISTS `farmacy` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `farmacy`;
-- MySQL dump 10.13  Distrib 5.6.17, for Win32 (x86)
--
-- Host: localhost    Database: farmacy
-- ------------------------------------------------------
-- Server version	5.6.21-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `categories_medicaments`
--

DROP TABLE IF EXISTS `categories_medicaments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `categories_medicaments` (
  `idCategory` int(11) NOT NULL AUTO_INCREMENT,
  `nameCategory` varchar(45) NOT NULL,
  PRIMARY KEY (`idCategory`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categories_medicaments`
--

LOCK TABLES `categories_medicaments` WRITE;
/*!40000 ALTER TABLE `categories_medicaments` DISABLE KEYS */;
INSERT INTO `categories_medicaments` VALUES (2,'vitamine'),(4,'antimycotic'),(5,'mucolitic'),(9,'antibiotic'),(12,'cosmetics');
/*!40000 ALTER TABLE `categories_medicaments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `clients`
--

DROP TABLE IF EXISTS `clients`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `clients` (
  `idClient` int(11) NOT NULL AUTO_INCREMENT,
  `nameClient` varchar(45) NOT NULL,
  `accountClient` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`idClient`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `clients`
--

LOCK TABLES `clients` WRITE;
/*!40000 ALTER TABLE `clients` DISABLE KEYS */;
INSERT INTO `clients` VALUES (1,'Persoana Fizica','*'),(11,'cccdd','');
/*!40000 ALTER TABLE `clients` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `farmacybalance`
--

DROP TABLE IF EXISTS `farmacybalance`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `farmacybalance` (
  `IdFarmacy` int(11) NOT NULL,
  `Balance` double unsigned DEFAULT NULL,
  `AdminPassword` varchar(45) DEFAULT NULL,
  `ManagerPassword` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`IdFarmacy`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `farmacybalance`
--

LOCK TABLES `farmacybalance` WRITE;
/*!40000 ALTER TABLE `farmacybalance` DISABLE KEYS */;
INSERT INTO `farmacybalance` VALUES (1,0,'111','');
/*!40000 ALTER TABLE `farmacybalance` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `invoices_p_details`
--

DROP TABLE IF EXISTS `invoices_p_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `invoices_p_details` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `idInvoiceP` int(11) NOT NULL,
  `idMedicament` int(11) NOT NULL,
  `quantity` double NOT NULL,
  `unitPrice` double NOT NULL,
  `total` double NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idInvoice_idx` (`idInvoiceP`),
  KEY `idMedicament_idx` (`idMedicament`),
  CONSTRAINT `idInvoiceP` FOREIGN KEY (`idInvoiceP`) REFERENCES `invoices_purchases` (`idInvoiceP`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `idMedicament` FOREIGN KEY (`idMedicament`) REFERENCES `medicaments` (`idMedicament`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `invoices_p_details`
--

LOCK TABLES `invoices_p_details` WRITE;
/*!40000 ALTER TABLE `invoices_p_details` DISABLE KEYS */;
INSERT INTO `invoices_p_details` VALUES (27,20,5,100,1,100),(28,22,5,19,1,19),(29,23,7,5,1,5),(30,20,6,200,2.4,480);
/*!40000 ALTER TABLE `invoices_p_details` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `invoices_purchases`
--

DROP TABLE IF EXISTS `invoices_purchases`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `invoices_purchases` (
  `idInvoiceP` int(11) NOT NULL AUTO_INCREMENT,
  `idSupplier` int(11) DEFAULT NULL,
  `dateInvoiceP` date NOT NULL,
  `totalInvoiceP` double NOT NULL,
  PRIMARY KEY (`idInvoiceP`),
  KEY `idSupp_idx` (`idSupplier`),
  CONSTRAINT `idSupp` FOREIGN KEY (`idSupplier`) REFERENCES `suppliers` (`idSupplier`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `invoices_purchases`
--

LOCK TABLES `invoices_purchases` WRITE;
/*!40000 ALTER TABLE `invoices_purchases` DISABLE KEYS */;
INSERT INTO `invoices_purchases` VALUES (20,14,'2014-12-26',580),(22,11,'2015-01-14',19),(23,11,'2015-01-21',5);
/*!40000 ALTER TABLE `invoices_purchases` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `invoices_s_details`
--

DROP TABLE IF EXISTS `invoices_s_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `invoices_s_details` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `idInvoiceS` int(11) NOT NULL,
  `idMedicament` int(11) NOT NULL,
  `quantity` double NOT NULL,
  `saleUnitPrice` double NOT NULL,
  `total` double NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idInvoiceS_idx` (`idInvoiceS`),
  KEY `idMedicament_idx` (`idMedicament`),
  KEY `idMed_idx` (`idMedicament`),
  CONSTRAINT `idInvoiceS` FOREIGN KEY (`idInvoiceS`) REFERENCES `invoices_sales` (`idInvoiceS`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `idMed` FOREIGN KEY (`idMedicament`) REFERENCES `medicaments` (`idMedicament`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=44 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `invoices_s_details`
--

LOCK TABLES `invoices_s_details` WRITE;
/*!40000 ALTER TABLE `invoices_s_details` DISABLE KEYS */;
INSERT INTO `invoices_s_details` VALUES (1,1,5,26,1.2,31.2),(2,3,5,6,1.2,7.2),(4,1,6,11,2.4,26.4),(5,3,7,2,1.5,12),(8,3,6,4,2.4,9.6),(10,5,6,12,2.4,28.8),(13,9,6,3,2.4,7.2),(14,17,5,2,1.2,2.4),(15,1,8,0,4.8,28.8),(20,17,6,5,2.4,12),(23,17,7,1,1.5,1.5),(25,17,6,2,2.4,4.8),(26,17,5,2,1.2,2.4),(27,17,6,3,2.4,7.2),(31,21,8,2,4.8,9.6),(35,22,5,3,1.2,3.6),(39,27,5,3,1.2,3.6);
/*!40000 ALTER TABLE `invoices_s_details` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `invoices_sales`
--

DROP TABLE IF EXISTS `invoices_sales`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `invoices_sales` (
  `idInvoiceS` int(11) NOT NULL AUTO_INCREMENT,
  `idClient` int(11) DEFAULT NULL,
  `dateInvoiceS` date NOT NULL,
  `totalInvoiceS` double NOT NULL,
  PRIMARY KEY (`idInvoiceS`),
  KEY `idClient_idx` (`idClient`),
  CONSTRAINT `idClient` FOREIGN KEY (`idClient`) REFERENCES `clients` (`idClient`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `invoices_sales`
--

LOCK TABLES `invoices_sales` WRITE;
/*!40000 ALTER TABLE `invoices_sales` DISABLE KEYS */;
INSERT INTO `invoices_sales` VALUES (1,1,'2014-12-26',86.40000000000003),(3,11,'2015-01-13',28.80000000000001),(5,1,'2015-01-10',28.799999999999994),(9,1,'2015-01-20',7.2),(17,1,'2015-01-20',30.3),(18,1,'2015-02-11',-0.000000000000002220446049250313),(21,1,'2015-02-11',9.599999999999998),(22,1,'2015-02-11',3.6),(27,1,'2015-02-11',3.6);
/*!40000 ALTER TABLE `invoices_sales` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `medicament_analogs`
--

DROP TABLE IF EXISTS `medicament_analogs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `medicament_analogs` (
  `idMedicament` int(11) NOT NULL,
  `idMedicamentAnalog` int(11) NOT NULL,
  PRIMARY KEY (`idMedicament`,`idMedicamentAnalog`),
  KEY `idMedAnalog_idx` (`idMedicament`),
  KEY `idMedAnalog_idx1` (`idMedicamentAnalog`),
  CONSTRAINT `idMedAnalog` FOREIGN KEY (`idMedicamentAnalog`) REFERENCES `medicaments` (`idMedicament`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `idMedOrig` FOREIGN KEY (`idMedicament`) REFERENCES `medicaments` (`idMedicament`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `medicament_analogs`
--

LOCK TABLES `medicament_analogs` WRITE;
/*!40000 ALTER TABLE `medicament_analogs` DISABLE KEYS */;
INSERT INTO `medicament_analogs` VALUES (5,6),(5,7),(5,8),(6,5),(7,5),(8,5);
/*!40000 ALTER TABLE `medicament_analogs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `medicaments`
--

DROP TABLE IF EXISTS `medicaments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `medicaments` (
  `idMedicament` int(11) NOT NULL AUTO_INCREMENT,
  `nameMedicament` varchar(45) NOT NULL,
  `idCategory` int(11) DEFAULT NULL,
  `latinName` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`idMedicament`),
  KEY `idCateg_idx` (`idCategory`),
  CONSTRAINT `idCateg` FOREIGN KEY (`idCategory`) REFERENCES `categories_medicaments` (`idCategory`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `medicaments`
--

LOCK TABLES `medicaments` WRITE;
/*!40000 ALTER TABLE `medicaments` DISABLE KEYS */;
INSERT INTO `medicaments` VALUES (5,'Aspiriin',2,'Aspiriin'),(6,'Clotrimazol',2,'Clotr'),(7,'Mucaltina',NULL,'Muc'),(8,'AmiPlus',9,'Amoxicilnum');
/*!40000 ALTER TABLE `medicaments` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `medicaments_AINS` AFTER INSERT ON `medicaments` FOR EACH ROW
BEGIN
INSERT INTO medicaments_prices SET idMedicament=NEW.idMedicament;
INSERT INTO stock_medicaments SET idMedicament=NEW.idMedicament;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `medicaments_prices`
--

DROP TABLE IF EXISTS `medicaments_prices`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `medicaments_prices` (
  `idMedicament` int(11) NOT NULL,
  `unitPrice` double DEFAULT '0',
  `margin` double DEFAULT '0',
  `saleUnitPrice` double DEFAULT '0',
  PRIMARY KEY (`idMedicament`),
  KEY `idMed_idx` (`idMedicament`),
  CONSTRAINT `idMedic` FOREIGN KEY (`idMedicament`) REFERENCES `medicaments` (`idMedicament`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `medicaments_prices`
--

LOCK TABLES `medicaments_prices` WRITE;
/*!40000 ALTER TABLE `medicaments_prices` DISABLE KEYS */;
INSERT INTO `medicaments_prices` VALUES (5,1,1.2,1.2),(6,2,1.2,2.4),(7,1,1.5,1.5),(8,4,1.2,4.8);
/*!40000 ALTER TABLE `medicaments_prices` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `stock_medicaments`
--

DROP TABLE IF EXISTS `stock_medicaments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `stock_medicaments` (
  `idMedicament` int(11) NOT NULL,
  `balance` double unsigned DEFAULT '0',
  PRIMARY KEY (`idMedicament`),
  CONSTRAINT `idMeds` FOREIGN KEY (`idMedicament`) REFERENCES `medicaments` (`idMedicament`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `stock_medicaments`
--

LOCK TABLES `stock_medicaments` WRITE;
/*!40000 ALTER TABLE `stock_medicaments` DISABLE KEYS */;
INSERT INTO `stock_medicaments` VALUES (5,70),(6,165),(7,0),(8,0);
/*!40000 ALTER TABLE `stock_medicaments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `suppliers`
--

DROP TABLE IF EXISTS `suppliers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `suppliers` (
  `idSupplier` int(11) NOT NULL AUTO_INCREMENT,
  `nameSupplier` varchar(45) NOT NULL DEFAULT 'Firma',
  `accountSupplier` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`idSupplier`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `suppliers`
--

LOCK TABLES `suppliers` WRITE;
/*!40000 ALTER TABLE `suppliers` DISABLE KEYS */;
INSERT INTO `suppliers` VALUES (11,'qqq',''),(12,'1111',''),(14,'Felicia','1.1/1/2014');
/*!40000 ALTER TABLE `suppliers` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2015-02-12 16:52:46

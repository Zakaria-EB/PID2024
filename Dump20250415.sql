CREATE DATABASE  IF NOT EXISTS `reservations` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `reservations`;
-- MySQL dump 10.13  Distrib 8.0.40, for Win64 (x86_64)
--
-- Host: localhost    Database: reservations
-- ------------------------------------------------------
-- Server version	8.0.40

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `artist_type`
--

DROP TABLE IF EXISTS `artist_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `artist_type` (
  `id` int NOT NULL AUTO_INCREMENT,
  `artist_id` int NOT NULL,
  `type_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `artist_type_artist_id_ IDX_3060D1B6B7970CF8_fk_artists_id` (`artist_id`),
  KEY `artist_type_type_id_ IDX_3060D1B6C54C8C93_fk_types_id` (`type_id`),
  CONSTRAINT `artist_type_artist_id_ 3060D1B6B7970CF8_fk_artists_id` FOREIGN KEY (`artist_id`) REFERENCES `artists` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `artist_type_type_id_ 3060D1B6C54C8C93 _fk_artists_id` FOREIGN KEY (`type_id`) REFERENCES `types` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `artist_type`
--

LOCK TABLES `artist_type` WRITE;
/*!40000 ALTER TABLE `artist_type` DISABLE KEYS */;
INSERT INTO `artist_type` VALUES (1,1,1),(2,2,1),(3,1,2),(4,2,2),(5,1,3),(6,3,1),(7,4,2),(8,5,3),(9,6,3),(10,7,3),(11,8,3),(12,9,3),(13,10,1),(14,11,2),(15,10,3),(16,12,1),(17,13,1),(18,12,3),(19,13,3);
/*!40000 ALTER TABLE `artist_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `artist_type_show`
--

DROP TABLE IF EXISTS `artist_type_show`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `artist_type_show` (
  `id` int NOT NULL AUTO_INCREMENT,
  `artist_type_id` int NOT NULL,
  `show_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `IDX_9F6421FED0C1FC64` (`show_id`),
  KEY `IDX_9F6421FE7203D2A4` (`artist_type_id`),
  CONSTRAINT `FK_9F6421FE7203D2A4` FOREIGN KEY (`artist_type_id`) REFERENCES `artist_type` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `FK_9F6421FED0C1FC64` FOREIGN KEY (`show_id`) REFERENCES `shows` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `artist_type_show`
--

LOCK TABLES `artist_type_show` WRITE;
/*!40000 ALTER TABLE `artist_type_show` DISABLE KEYS */;
INSERT INTO `artist_type_show` VALUES (1,1,1),(2,2,1),(3,3,1),(4,4,1),(5,5,1),(6,6,2),(7,7,2),(8,8,2),(9,9,2),(10,10,2),(11,11,2),(12,12,2),(13,13,3),(14,14,3),(15,15,3),(16,4,4),(17,16,4),(18,17,4),(19,18,4),(20,19,4);
/*!40000 ALTER TABLE `artist_type_show` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `artists`
--

DROP TABLE IF EXISTS `artists`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `artists` (
  `id` int NOT NULL AUTO_INCREMENT,
  `firstname` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `lastname` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `artists`
--

LOCK TABLES `artists` WRITE;
/*!40000 ALTER TABLE `artists` DISABLE KEYS */;
INSERT INTO `artists` VALUES (1,'Danielle','Marcelin'),(2,'Philippe','Laurent'),(3,'Marius','Von Mayenburg'),(4,'Olivier','Boudon'),(5,'Anne Marie','Loop'),(6,'Pietro','Varasso'),(7,'Laurent','Caron'),(8,'Élena','Perez'),(9,'Guillaume','Alexandre'),(10,'Claude','Semal'),(11,'Laurence','Warin'),(12,'Pierre','Wayburn'),(13,'Gwendoline','Gauthier');
/*!40000 ALTER TABLE `artists` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `flyway_schema_history`
--

DROP TABLE IF EXISTS `flyway_schema_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `flyway_schema_history` (
  `installed_rank` int NOT NULL,
  `version` varchar(50) DEFAULT NULL,
  `description` varchar(200) NOT NULL,
  `type` varchar(20) NOT NULL,
  `script` varchar(1000) NOT NULL,
  `checksum` int DEFAULT NULL,
  `installed_by` varchar(100) NOT NULL,
  `installed_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `execution_time` int NOT NULL,
  `success` tinyint(1) NOT NULL,
  PRIMARY KEY (`installed_rank`),
  KEY `flyway_schema_history_s_idx` (`success`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `flyway_schema_history`
--

LOCK TABLES `flyway_schema_history` WRITE;
/*!40000 ALTER TABLE `flyway_schema_history` DISABLE KEYS */;
INSERT INTO `flyway_schema_history` VALUES (1,'1.1','create artists table','SQL','V1_1__create_artists_table.sql',-1928428595,'root','2025-04-15 18:58:46',75,1),(2,'1.2','insert artists table','SQL','V1_2__insert_artists_table.sql',-280175186,'root','2025-04-15 18:58:46',11,1),(3,'2.1','create users table','SQL','V2_1__create_users_table.sql',513360628,'root','2025-04-15 18:58:46',106,1),(4,'2.2','insert users table','SQL','V2_2__insert_users_table.sql',-1294094065,'root','2025-04-15 18:58:46',9,1),(5,'3.1','create localities table','SQL','V3_1__create_localities_table.sql',175429460,'root','2025-04-15 18:58:46',121,1),(6,'3.2','insert localities table','SQL','V3_2__insert_localities_table.sql',805785997,'root','2025-04-15 18:58:46',8,1),(7,'4.1','create roles table','SQL','V4_1__create_roles_table.sql',239598783,'root','2025-04-15 18:58:47',59,1),(8,'4.2','insert roles table','SQL','V4_2__insert_roles_table.sql',-1624113663,'root','2025-04-15 18:58:47',7,1),(9,'5.1','create types table','SQL','V5_1__create_types_table.sql',1925872095,'root','2025-04-15 18:58:47',33,1),(10,'5.2','insert types table','SQL','V5_2__insert_types_table.sql',-126045216,'root','2025-04-15 18:58:47',7,1),(11,'6.1','create locations table','SQL','V6_1__create_locations_table.sql',1736301610,'root','2025-04-15 18:58:47',185,1),(12,'6.2','insert locations table','SQL','V6_2__insert_locations_table.sql',1508143262,'root','2025-04-15 18:58:47',6,1),(13,'7.1','create shows table','SQL','V7_1__create_shows_table.sql',-892313632,'root','2025-04-15 18:58:47',237,1),(14,'7.2','insert shows table','SQL','V7_2__insert_shows_table.sql',-2085968838,'root','2025-04-15 18:58:47',6,1),(15,'8.1','create representations table','SQL','V8_1__create_representations_table.sql',-1708055491,'root','2025-04-15 18:58:48',310,1),(16,'8.2','create representations table','SQL','V8_2__create_representations_table.sql',1896858991,'root','2025-04-15 18:58:48',5,1),(17,'9.1','create artist type table','SQL','V9_1__create_artist_type_table.sql',1676507029,'root','2025-04-15 18:58:48',329,1),(18,'9.2','insert artist type table','SQL','V9_2__insert_artist_type_table.sql',-1003207613,'root','2025-04-15 18:58:48',6,1),(19,'10.1','create artist type show table','SQL','V10_1__create_artist_type_show_table.sql',220468303,'root','2025-04-15 18:58:48',177,1),(20,'10.2','insert artist type show table','SQL','V10_2__insert_artist_type_show_table.sql',-31123080,'root','2025-04-15 18:58:48',8,1),(21,'11.2','create user role table','SQL','V11_2__create_user_role_table.sql',-330443967,'root','2025-04-15 18:58:49',290,1),(22,'12.1','insert user role table','SQL','V12_1__insert_user_role_table.sql',425274378,'root','2025-04-15 18:58:49',4,1),(23,'12.2','create reservations table','SQL','V12_2__create_reservations_table.sql',1726137537,'root','2025-04-15 18:58:49',323,1),(24,'13.1','insert reservations table','SQL','V13_1__insert_reservations_table.sql',2074592799,'root','2025-04-15 18:58:49',4,1),(25,'14.1','create tags table','SQL','V14_1__create_tags_table.sql',1909949012,'root','2025-04-15 18:58:49',343,1),(26,'14.2','insert tags table','SQL','V14_2__insert_tags_table.sql',-1268063841,'root','2025-04-15 18:58:49',12,1);
/*!40000 ALTER TABLE `flyway_schema_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `localities`
--

DROP TABLE IF EXISTS `localities`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `localities` (
  `id` int NOT NULL AUTO_INCREMENT,
  `postal_code` varchar(6) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `locality` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `localities`
--

LOCK TABLES `localities` WRITE;
/*!40000 ALTER TABLE `localities` DISABLE KEYS */;
INSERT INTO `localities` VALUES (1,'1000','Bruxelles'),(2,'1020','Laeken'),(3,'1030','Schaerbeek'),(4,'1170','Watermael-Boistfort');
/*!40000 ALTER TABLE `localities` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `locations`
--

DROP TABLE IF EXISTS `locations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `locations` (
  `id` int NOT NULL AUTO_INCREMENT,
  `slug` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `designation` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `locality_id` int NOT NULL,
  `website` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `phone` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `slug` (`slug`),
  KEY `locations_locality_id_22dd0b44_fk_localities_id` (`locality_id`),
  CONSTRAINT `locations_locality_id_22dd0b44_fk_localities_id` FOREIGN KEY (`locality_id`) REFERENCES `localities` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `locations`
--

LOCK TABLES `locations` WRITE;
/*!40000 ALTER TABLE `locations` DISABLE KEYS */;
INSERT INTO `locations` VALUES (1,'espace-delvaux-la-venerie','Espace Delvaux / La Vénerie','3 rue Gratès',4,'https://www.lavenerie.be','+32 (0)2/663.85.50'),(2,'dexia-art-center','Dexia Art Center','50 rue de l\'Ecuyer',1,NULL,NULL),(3,'la-samaritaine','La Samaritaine','16 rue de la samaritaine',1,'http://www.lasamaritaine.be/',NULL),(4,'espace-magh','Espace Magh','17 rue du Poinçon',1,'http://www.espacemagh.be','+32 (0)2/274.05.10');
/*!40000 ALTER TABLE `locations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `representations`
--

DROP TABLE IF EXISTS `representations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `representations` (
  `id` int NOT NULL AUTO_INCREMENT,
  `show_id` int NOT NULL,
  `location_id` int DEFAULT NULL,
  `when` datetime(6) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `representations_location_id_a6832141_fk_locations_id` (`location_id`),
  KEY `representations_show_id_a6832141_fk_shows_id` (`show_id`),
  CONSTRAINT `representations_location_id_a6832141_fk_locations_id` FOREIGN KEY (`location_id`) REFERENCES `locations` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `representations_show_id_a6832141_fk_shows_id` FOREIGN KEY (`show_id`) REFERENCES `shows` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `representations`
--

LOCK TABLES `representations` WRITE;
/*!40000 ALTER TABLE `representations` DISABLE KEYS */;
INSERT INTO `representations` VALUES (1,1,1,'2012-10-12 13:30:00.000000'),(2,1,2,'2012-10-12 20:30:00.000000'),(3,2,NULL,'2012-10-02 20:30:00.000000'),(4,3,NULL,'2012-10-16 20:30:00.000000');
/*!40000 ALTER TABLE `representations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reservations`
--

DROP TABLE IF EXISTS `reservations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reservations` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `representation_id` int NOT NULL,
  `places` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `reservations_user_id` (`user_id`),
  KEY `reservations_representation_id` (`representation_id`),
  CONSTRAINT `reservations_representation_id` FOREIGN KEY (`representation_id`) REFERENCES `representations` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `reservations_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reservations`
--

LOCK TABLES `reservations` WRITE;
/*!40000 ALTER TABLE `reservations` DISABLE KEYS */;
INSERT INTO `reservations` VALUES (1,2,1,3),(2,2,2,5);
/*!40000 ALTER TABLE `reservations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `roles` (
  `id` int NOT NULL AUTO_INCREMENT,
  `role` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `role` (`role`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roles`
--

LOCK TABLES `roles` WRITE;
/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
INSERT INTO `roles` VALUES (1,'admin'),(3,'affiliate'),(2,'member');
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `show_tag`
--

DROP TABLE IF EXISTS `show_tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `show_tag` (
  `show_id` int NOT NULL,
  `tag_id` int NOT NULL,
  PRIMARY KEY (`show_id`,`tag_id`),
  KEY `show_tag_show_id` (`show_id`),
  KEY `show_tag_tag_id` (`tag_id`),
  CONSTRAINT `show_tag_show_id` FOREIGN KEY (`show_id`) REFERENCES `shows` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `show_tag_tag_id` FOREIGN KEY (`tag_id`) REFERENCES `tags` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `show_tag`
--

LOCK TABLES `show_tag` WRITE;
/*!40000 ALTER TABLE `show_tag` DISABLE KEYS */;
INSERT INTO `show_tag` VALUES (1,1),(1,5),(1,7),(1,8),(2,3),(2,6),(2,7),(3,2),(3,4),(4,2),(4,3);
/*!40000 ALTER TABLE `show_tag` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shows`
--

DROP TABLE IF EXISTS `shows`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shows` (
  `id` int NOT NULL AUTO_INCREMENT,
  `slug` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` longtext COLLATE utf8mb4_unicode_ci,
  `poster_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `bookable` tinyint(1) NOT NULL,
  `price` decimal(10,2) DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `location_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `slug` (`slug`),
  KEY `shows_location_id_a6832141_fk_locations_id` (`location_id`),
  CONSTRAINT `shows_location_id_a6832141_fk_locations_id` FOREIGN KEY (`location_id`) REFERENCES `locations` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shows`
--

LOCK TABLES `shows` WRITE;
/*!40000 ALTER TABLE `shows` DISABLE KEYS */;
INSERT INTO `shows` VALUES (1,'ayiti','Ayiti','Un homme est bloqué à l’aéroport.\n Questionné par les douaniers, il doit alors justifier son identité, et surtout prouver qu\'il est haïtien – qu\'est-ce qu\'être haïtien ?','ayiti.jpg',1,9.00,'2020-04-21 19:08:44.583000',NULL,1),(2,'cible-mouvante','Cible mouvante','Dans ce « thriller d’anticipation », des adultes semblent alimenter et véhiculer une crainte féroce envers les enfants âgés entre 10 et 12 ans.','cible.jpg',1,9.00,'2020-04-21 19:08:53.156000',NULL,2),(3,'ceci-nest-pas-un-chanteur-belge','Ceci n\'est pas un chanteur belge','Non peut-être ?!\nEntre Magritte (pour le surréalisme comique) et Maigret (pour le réalisme mélancolique), ce dixième opus semalien propose quatorze nouvelles chansons mêlées à de petits textes humoristiques et à quelques fortes images poétiques.','claudebelgesaison220.jpg',1,5.50,'2020-04-21 19:08:53.189000','2020-04-21 19:08:53.189000',NULL),(4,'manneke','Manneke… !','A tour de rôle, Pierre se joue de ses oncles, tantes, grands-parents et surtout de sa mère.','wayburn.jpg',1,10.50,'2020-04-21 19:09:02.426000','2020-04-21 19:09:02.426000',3);
/*!40000 ALTER TABLE `shows` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tags`
--

DROP TABLE IF EXISTS `tags`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tags` (
  `id` int NOT NULL AUTO_INCREMENT,
  `tag` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `tag` (`tag`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tags`
--

LOCK TABLES `tags` WRITE;
/*!40000 ALTER TABLE `tags` DISABLE KEYS */;
INSERT INTO `tags` VALUES (6,'anticipation'),(4,'chansons'),(3,'enfants'),(5,'haïti'),(2,'humour'),(8,'new'),(7,'société'),(1,'théâtre');
/*!40000 ALTER TABLE `tags` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `types`
--

DROP TABLE IF EXISTS `types`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `types` (
  `id` int NOT NULL AUTO_INCREMENT,
  `type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `types`
--

LOCK TABLES `types` WRITE;
/*!40000 ALTER TABLE `types` DISABLE KEYS */;
INSERT INTO `types` VALUES (1,'auteur'),(2,'scénographe'),(3,'comédien');
/*!40000 ALTER TABLE `types` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_role`
--

DROP TABLE IF EXISTS `user_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_role` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `role_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `user_role_user_id` (`user_id`),
  KEY `user_role_role_id` (`role_id`),
  CONSTRAINT `user_role_role_id` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `user_role_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_role`
--

LOCK TABLES `user_role` WRITE;
/*!40000 ALTER TABLE `user_role` DISABLE KEYS */;
INSERT INTO `user_role` VALUES (1,1,1),(2,2,2),(3,3,3);
/*!40000 ALTER TABLE `user_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `login` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `firstname` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `lastname` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `langue` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_at` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'bob','$2b$12$tqiW87YjQ2jZlV0LtaGScuhF.hUUErjMkk5tAg3u5reQbLWb5yJKC','Bob','Sull','bob@sull.com','fr','2010-01-01 12:00:00'),(2,'lana','$2b$12$tqiW87YjQ2jZlV0LtaGScuhF.hUUErjMkk5tAg3u5reQbLWb5yJKC','Lana','Sull','lana@sull.com','fr','2010-01-01 12:00:00'),(3,'affiliate','$2b$12$tqiW87YjQ2jZlV0LtaGScuhF.hUUErjMkk5tAg3u5reQbLWb5yJKC','Affi','Liate','contact@affiliate.com','fr','2020-01-01 12:00:00');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-04-15 21:40:51

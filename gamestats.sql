-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: May 23, 2024 at 04:55 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.0.30

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `alfatraining`
--

-- --------------------------------------------------------

--
-- Table structure for table `gamestats`
--

CREATE TABLE `gamestats` (
  `ID` int(11) NOT NULL,
  `WORD` text NOT NULL,
  `COMBINATION` text NOT NULL,
  `POPULARITY` int(11) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_ci;

--
-- Dumping data for table `gamestats`
--

INSERT INTO `gamestats` (`ID`, `WORD`, `COMBINATION`, `POPULARITY`) VALUES
(1, 'quadrat', 'art', 2),
(2, 'quadrat', 'rad', 1),
(3, 'kombinations', 'mini', 5),
(4, 'kombinations', 'mit', 4),
(5, 'kombinations', 'monats', 3),
(6, 'kombinations', 'not', 2),
(7, 'kombinations', 'obst', 1),
(8, 'schlitten', 'sitten', 1),
(9, 'schlitten', 'steil', 1),
(10, 'schlitten', 'tisch', 1),
(11, 'legitimations', 'stein', 1),
(12, 'legitimations', 'titel', 3),
(13, 'legitimations', 'tomaten', 1),
(14, 'legitimations', 'ton', 1),
(15, 'legitimations', 'titan', 1),
(16, 'legitimations', 'smog', 1),
(17, 'milliarden', 'lern', 1),
(18, 'milliarden', 'mai', 1),
(19, 'milliarden', 'nadel', 1),
(20, 'milliarden', 'rand', 1),
(21, 'leichen', 'ehe', 1),
(22, 'leichen', 'ein', 1),
(23, 'gebraucht', 'uhr', 1),
(24, 'gebraucht', 'tauch', 1),
(25, 'gebraucht', 'tabu', 1),
(26, 'autogramm', 'traum', 1),
(27, 'autogramm', 'trag', 1),
(28, 'verbands', 'verb', 1),
(29, 'verbands', 'versand', 1),
(30, 'verbands', 'sand', 1),
(31, 'schnell', 'elch', 1),
(32, 'schnell', 'seh', 1),
(33, 'touristik', 'tor', 1),
(34, 'touristik', 'ski', 1),
(35, 'touristik', 'kur', 1),
(36, 'behilfs', 'seh', 1),
(37, 'behilfs', 'seil', 1),
(38, 'behilfs', 'leih', 1),
(39, 'behilfs', 'hilfs', 1),
(40, 'behilfs', 'hilfe', 1);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `gamestats`
--
ALTER TABLE `gamestats`
  ADD PRIMARY KEY (`ID`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `gamestats`
--
ALTER TABLE `gamestats`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=41;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

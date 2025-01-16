USE [master]
GO
/****** Object:  Database [chatroom]    Script Date: 1/16/2025 9:07:01 AM ******/
CREATE DATABASE [chatroom]
 CONTAINMENT = NONE
 ON  PRIMARY 
( NAME = N'chatroom', FILENAME = N'C:\Program Files\Microsoft SQL Server\MSSQL16.MSSQLSERVER\MSSQL\DATA\chatroom.mdf' , SIZE = 8192KB , MAXSIZE = UNLIMITED, FILEGROWTH = 65536KB )
 LOG ON 
( NAME = N'chatroom_log', FILENAME = N'C:\Program Files\Microsoft SQL Server\MSSQL16.MSSQLSERVER\MSSQL\DATA\chatroom_log.ldf' , SIZE = 8192KB , MAXSIZE = 2048GB , FILEGROWTH = 65536KB )
 WITH CATALOG_COLLATION = DATABASE_DEFAULT, LEDGER = OFF
GO
ALTER DATABASE [chatroom] SET COMPATIBILITY_LEVEL = 160
GO
IF (1 = FULLTEXTSERVICEPROPERTY('IsFullTextInstalled'))
begin
EXEC [chatroom].[dbo].[sp_fulltext_database] @action = 'enable'
end
GO
ALTER DATABASE [chatroom] SET ANSI_NULL_DEFAULT OFF 
GO
ALTER DATABASE [chatroom] SET ANSI_NULLS OFF 
GO
ALTER DATABASE [chatroom] SET ANSI_PADDING OFF 
GO
ALTER DATABASE [chatroom] SET ANSI_WARNINGS OFF 
GO
ALTER DATABASE [chatroom] SET ARITHABORT OFF 
GO
ALTER DATABASE [chatroom] SET AUTO_CLOSE OFF 
GO
ALTER DATABASE [chatroom] SET AUTO_SHRINK OFF 
GO
ALTER DATABASE [chatroom] SET AUTO_UPDATE_STATISTICS ON 
GO
ALTER DATABASE [chatroom] SET CURSOR_CLOSE_ON_COMMIT OFF 
GO
ALTER DATABASE [chatroom] SET CURSOR_DEFAULT  GLOBAL 
GO
ALTER DATABASE [chatroom] SET CONCAT_NULL_YIELDS_NULL OFF 
GO
ALTER DATABASE [chatroom] SET NUMERIC_ROUNDABORT OFF 
GO
ALTER DATABASE [chatroom] SET QUOTED_IDENTIFIER OFF 
GO
ALTER DATABASE [chatroom] SET RECURSIVE_TRIGGERS OFF 
GO
ALTER DATABASE [chatroom] SET  DISABLE_BROKER 
GO
ALTER DATABASE [chatroom] SET AUTO_UPDATE_STATISTICS_ASYNC OFF 
GO
ALTER DATABASE [chatroom] SET DATE_CORRELATION_OPTIMIZATION OFF 
GO
ALTER DATABASE [chatroom] SET TRUSTWORTHY OFF 
GO
ALTER DATABASE [chatroom] SET ALLOW_SNAPSHOT_ISOLATION OFF 
GO
ALTER DATABASE [chatroom] SET PARAMETERIZATION SIMPLE 
GO
ALTER DATABASE [chatroom] SET READ_COMMITTED_SNAPSHOT OFF 
GO
ALTER DATABASE [chatroom] SET HONOR_BROKER_PRIORITY OFF 
GO
ALTER DATABASE [chatroom] SET RECOVERY FULL 
GO
ALTER DATABASE [chatroom] SET  MULTI_USER 
GO
ALTER DATABASE [chatroom] SET PAGE_VERIFY CHECKSUM  
GO
ALTER DATABASE [chatroom] SET DB_CHAINING OFF 
GO
ALTER DATABASE [chatroom] SET FILESTREAM( NON_TRANSACTED_ACCESS = OFF ) 
GO
ALTER DATABASE [chatroom] SET TARGET_RECOVERY_TIME = 60 SECONDS 
GO
ALTER DATABASE [chatroom] SET DELAYED_DURABILITY = DISABLED 
GO
ALTER DATABASE [chatroom] SET ACCELERATED_DATABASE_RECOVERY = OFF  
GO
EXEC sys.sp_db_vardecimal_storage_format N'chatroom', N'ON'
GO
ALTER DATABASE [chatroom] SET QUERY_STORE = ON
GO
ALTER DATABASE [chatroom] SET QUERY_STORE (OPERATION_MODE = READ_WRITE, CLEANUP_POLICY = (STALE_QUERY_THRESHOLD_DAYS = 30), DATA_FLUSH_INTERVAL_SECONDS = 900, INTERVAL_LENGTH_MINUTES = 60, MAX_STORAGE_SIZE_MB = 1000, QUERY_CAPTURE_MODE = AUTO, SIZE_BASED_CLEANUP_MODE = AUTO, MAX_PLANS_PER_QUERY = 200, WAIT_STATS_CAPTURE_MODE = ON)
GO
USE [chatroom]
GO
/****** Object:  Table [dbo].[block]    Script Date: 1/16/2025 9:07:01 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[block](
	[id] [int] NOT NULL,
	[blocker_id] [int] NOT NULL,
	[blocked_id] [int] NOT NULL,
	[create_at] [datetime] NOT NULL,
 CONSTRAINT [PK_block] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
 CONSTRAINT [UK_block] UNIQUE NONCLUSTERED 
(
	[blocker_id] ASC,
	[blocked_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[contact_request]    Script Date: 1/16/2025 9:07:01 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[contact_request](
	[id] [int] NOT NULL,
	[sender_id] [int] NOT NULL,
	[receiver_id] [int] NOT NULL,
	[create_at] [datetime] NOT NULL,
 CONSTRAINT [PK_contact_request] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
 CONSTRAINT [IX_contact_request] UNIQUE NONCLUSTERED 
(
	[receiver_id] ASC,
	[sender_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[contact_with]    Script Date: 1/16/2025 9:07:01 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[contact_with](
	[id] [int] NOT NULL,
	[contact_one] [int] NOT NULL,
	[contact_two] [int] NOT NULL,
	[create_at] [datetime] NOT NULL,
 CONSTRAINT [PK_contact_with] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
 CONSTRAINT [UK_contact_with] UNIQUE NONCLUSTERED 
(
	[contact_one] ASC,
	[contact_two] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[group]    Script Date: 1/16/2025 9:07:01 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[group](
	[id] [int] NOT NULL,
	[group_name] [nvarchar](50) NOT NULL,
	[create_at] [datetime] NOT NULL,
	[create_by] [int] NOT NULL,
 CONSTRAINT [PK_group] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[group_user]    Script Date: 1/16/2025 9:07:01 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[group_user](
	[id] [int] NOT NULL,
	[group_id] [int] NOT NULL,
	[user_id] [int] NOT NULL,
 CONSTRAINT [PK_group_user] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[message]    Script Date: 1/16/2025 9:07:01 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[message](
	[id] [int] NOT NULL,
	[sender_id] [int] NOT NULL,
	[receiver_id] [int] NOT NULL,
	[message_text] [text] NULL,
	[attachmen_url] [text] NULL,
	[create_at] [datetime] NOT NULL,
	[delete_at] [datetime] NULL,
	[update_at] [datetime] NULL,
	[is_read] [bigint] NOT NULL,
 CONSTRAINT [PK_message] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[user]    Script Date: 1/16/2025 9:07:01 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[user](
	[id] [int] NOT NULL,
	[first_name] [nvarchar](50) NOT NULL,
	[last_name] [nvarchar](50) NOT NULL,
	[username] [nchar](50) NOT NULL,
	[password] [nchar](50) NOT NULL,
	[email] [nvarchar](50) NOT NULL,
	[status] [bit] NOT NULL,
	[last_seen] [datetime] NOT NULL,
 CONSTRAINT [PK_user] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
ALTER TABLE [dbo].[block]  WITH CHECK ADD  CONSTRAINT [FK_block_user] FOREIGN KEY([blocker_id])
REFERENCES [dbo].[user] ([id])
GO
ALTER TABLE [dbo].[block] CHECK CONSTRAINT [FK_block_user]
GO
ALTER TABLE [dbo].[block]  WITH CHECK ADD  CONSTRAINT [FK_block_user1] FOREIGN KEY([blocked_id])
REFERENCES [dbo].[user] ([id])
GO
ALTER TABLE [dbo].[block] CHECK CONSTRAINT [FK_block_user1]
GO
ALTER TABLE [dbo].[contact_request]  WITH CHECK ADD  CONSTRAINT [FK_contact_request_user] FOREIGN KEY([sender_id])
REFERENCES [dbo].[user] ([id])
GO
ALTER TABLE [dbo].[contact_request] CHECK CONSTRAINT [FK_contact_request_user]
GO
ALTER TABLE [dbo].[contact_request]  WITH CHECK ADD  CONSTRAINT [FK_contact_request_user1] FOREIGN KEY([receiver_id])
REFERENCES [dbo].[user] ([id])
GO
ALTER TABLE [dbo].[contact_request] CHECK CONSTRAINT [FK_contact_request_user1]
GO
ALTER TABLE [dbo].[contact_with]  WITH CHECK ADD  CONSTRAINT [FK_contact_with_user] FOREIGN KEY([contact_one])
REFERENCES [dbo].[user] ([id])
GO
ALTER TABLE [dbo].[contact_with] CHECK CONSTRAINT [FK_contact_with_user]
GO
ALTER TABLE [dbo].[contact_with]  WITH CHECK ADD  CONSTRAINT [FK_contact_with_user1] FOREIGN KEY([contact_two])
REFERENCES [dbo].[user] ([id])
GO
ALTER TABLE [dbo].[contact_with] CHECK CONSTRAINT [FK_contact_with_user1]
GO
ALTER TABLE [dbo].[group]  WITH CHECK ADD  CONSTRAINT [FK_group_user] FOREIGN KEY([create_by])
REFERENCES [dbo].[user] ([id])
GO
ALTER TABLE [dbo].[group] CHECK CONSTRAINT [FK_group_user]
GO
ALTER TABLE [dbo].[group_user]  WITH CHECK ADD  CONSTRAINT [FK_group_user_group] FOREIGN KEY([group_id])
REFERENCES [dbo].[group] ([id])
GO
ALTER TABLE [dbo].[group_user] CHECK CONSTRAINT [FK_group_user_group]
GO
ALTER TABLE [dbo].[group_user]  WITH CHECK ADD  CONSTRAINT [FK_group_user_user] FOREIGN KEY([user_id])
REFERENCES [dbo].[user] ([id])
GO
ALTER TABLE [dbo].[group_user] CHECK CONSTRAINT [FK_group_user_user]
GO
ALTER TABLE [dbo].[message]  WITH CHECK ADD  CONSTRAINT [FK_message_group] FOREIGN KEY([receiver_id])
REFERENCES [dbo].[group] ([id])
GO
ALTER TABLE [dbo].[message] CHECK CONSTRAINT [FK_message_group]
GO
ALTER TABLE [dbo].[message]  WITH CHECK ADD  CONSTRAINT [FK_message_user] FOREIGN KEY([sender_id])
REFERENCES [dbo].[user] ([id])
GO
ALTER TABLE [dbo].[message] CHECK CONSTRAINT [FK_message_user]
GO
ALTER TABLE [dbo].[message]  WITH CHECK ADD  CONSTRAINT [FK_message_user1] FOREIGN KEY([receiver_id])
REFERENCES [dbo].[user] ([id])
GO
ALTER TABLE [dbo].[message] CHECK CONSTRAINT [FK_message_user1]
GO
USE [master]
GO
ALTER DATABASE [chatroom] SET  READ_WRITE 
GO

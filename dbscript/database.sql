
USE [chatroom]
GO
/****** Object:  Table [dbo].[block]    Script Date: 1/15/2025 8:53:21 PM ******/
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
/****** Object:  Table [dbo].[contact_request]    Script Date: 1/15/2025 8:53:21 PM ******/
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
/****** Object:  Table [dbo].[contact_with]    Script Date: 1/15/2025 8:53:21 PM ******/
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
/****** Object:  Table [dbo].[group]    Script Date: 1/15/2025 8:53:21 PM ******/
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
/****** Object:  Table [dbo].[group_user]    Script Date: 1/15/2025 8:53:21 PM ******/
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
/****** Object:  Table [dbo].[message]    Script Date: 1/15/2025 8:53:21 PM ******/
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
/****** Object:  Table [dbo].[user]    Script Date: 1/15/2025 8:53:21 PM ******/
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
	[status] [bigint] NOT NULL,
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

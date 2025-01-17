
USE [chatroom]
GO
/****** Object:  Table [dbo].[attachment]    Script Date: 1/17/2025 8:44:24 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[attachment](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[message_id] [bigint] NOT NULL,
	[upload_at] [datetime2](6) NOT NULL,
	[file_type] [varchar](255) NOT NULL,
	[file_url] [varchar](255) NOT NULL,
 CONSTRAINT [PK__attachme__3213E83FF933B636] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[block]    Script Date: 1/17/2025 8:44:24 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[block](
	[blocked_id] [bigint] NULL,
	[blocker_id] [bigint] NULL,
	[created_at] [datetime2](6) NOT NULL,
	[id] [bigint] IDENTITY(1,1) NOT NULL,
 CONSTRAINT [PK__block__3213E83F4F897F85] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[contact_request]    Script Date: 1/17/2025 8:44:24 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[contact_request](
	[is_accepted] [bit] NOT NULL,
	[created_at] [datetime2](6) NOT NULL,
	[delete_at] [datetime2](6) NULL,
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[receiver_id] [bigint] NOT NULL,
	[sender_id] [bigint] NOT NULL,
 CONSTRAINT [PK__contact___3213E83F3194844A] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[contact_with]    Script Date: 1/17/2025 8:44:24 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[contact_with](
	[contact_one_id] [bigint] NOT NULL,
	[contact_two_id] [bigint] NOT NULL,
	[created_at] [datetime2](6) NOT NULL,
	[deleted_at] [datetime2](6) NULL,
	[id] [bigint] IDENTITY(1,1) NOT NULL,
 CONSTRAINT [PK__contact___3213E83F4A5E2A73] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[conversation]    Script Date: 1/17/2025 8:44:24 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[conversation](
	[is_group] [bit] NOT NULL,
	[created_at] [datetime2](6) NOT NULL,
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[image_url] [varchar](255) NULL,
	[name] [varchar](255) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[conversation_member]    Script Date: 1/17/2025 8:44:24 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[conversation_member](
	[conversation_id] [bigint] NOT NULL,
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[joined_at] [datetime2](6) NOT NULL,
	[user_id] [bigint] NOT NULL,
	[role] [varchar](255) NOT NULL,
 CONSTRAINT [PK__conversa__3213E83F91E9462A] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[message]    Script Date: 1/17/2025 8:44:24 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[message](
	[is_read] [bit] NOT NULL,
	[conversation_id] [bigint] NOT NULL,
	[created_at] [datetime2](6) NOT NULL,
	[deleted_at] [datetime2](6) NULL,
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[updated_at] [datetime2](6) NULL,
	[user_id] [bigint] NOT NULL,
	[message_text] [varchar](255) NOT NULL,
 CONSTRAINT [PK__message__3213E83FE809D0FB] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[user]    Script Date: 1/17/2025 8:44:24 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[user](
	[id] [bigint] NOT NULL,
	[first_name] [varchar](255) NOT NULL,
	[last_name] [varchar](255) NOT NULL,
	[username] [varchar](255) NOT NULL,
	[hash_password] [varchar](255) NOT NULL,
	[email] [varchar](255) NOT NULL,
	[status] [bit] NOT NULL,
	[last_seen] [datetime2](6) NOT NULL,
	[profile_picture] [varchar](255) NULL,
 CONSTRAINT [PK_user] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
ALTER TABLE [dbo].[attachment]  WITH CHECK ADD  CONSTRAINT [FK_attachment_message] FOREIGN KEY([message_id])
REFERENCES [dbo].[message] ([id])
GO
ALTER TABLE [dbo].[attachment] CHECK CONSTRAINT [FK_attachment_message]
GO
ALTER TABLE [dbo].[block]  WITH CHECK ADD  CONSTRAINT [FK_block_user2] FOREIGN KEY([blocked_id])
REFERENCES [dbo].[user] ([id])
GO
ALTER TABLE [dbo].[block] CHECK CONSTRAINT [FK_block_user2]
GO
ALTER TABLE [dbo].[block]  WITH CHECK ADD  CONSTRAINT [FK_block_user3] FOREIGN KEY([blocker_id])
REFERENCES [dbo].[user] ([id])
GO
ALTER TABLE [dbo].[block] CHECK CONSTRAINT [FK_block_user3]
GO
ALTER TABLE [dbo].[contact_request]  WITH CHECK ADD  CONSTRAINT [FK_contact_request_user2] FOREIGN KEY([receiver_id])
REFERENCES [dbo].[user] ([id])
GO
ALTER TABLE [dbo].[contact_request] CHECK CONSTRAINT [FK_contact_request_user2]
GO
ALTER TABLE [dbo].[contact_request]  WITH CHECK ADD  CONSTRAINT [FK_contact_request_user3] FOREIGN KEY([sender_id])
REFERENCES [dbo].[user] ([id])
GO
ALTER TABLE [dbo].[contact_request] CHECK CONSTRAINT [FK_contact_request_user3]
GO
ALTER TABLE [dbo].[contact_with]  WITH CHECK ADD  CONSTRAINT [FK_contact_with_user2] FOREIGN KEY([contact_two_id])
REFERENCES [dbo].[user] ([id])
GO
ALTER TABLE [dbo].[contact_with] CHECK CONSTRAINT [FK_contact_with_user2]
GO
ALTER TABLE [dbo].[contact_with]  WITH CHECK ADD  CONSTRAINT [FK_contact_with_user3] FOREIGN KEY([contact_one_id])
REFERENCES [dbo].[user] ([id])
GO
ALTER TABLE [dbo].[contact_with] CHECK CONSTRAINT [FK_contact_with_user3]
GO
ALTER TABLE [dbo].[conversation_member]  WITH CHECK ADD  CONSTRAINT [FK_conversation_member_conversation] FOREIGN KEY([conversation_id])
REFERENCES [dbo].[conversation] ([id])
GO
ALTER TABLE [dbo].[conversation_member] CHECK CONSTRAINT [FK_conversation_member_conversation]
GO
ALTER TABLE [dbo].[conversation_member]  WITH CHECK ADD  CONSTRAINT [FK_conversation_member_user] FOREIGN KEY([user_id])
REFERENCES [dbo].[user] ([id])
GO
ALTER TABLE [dbo].[conversation_member] CHECK CONSTRAINT [FK_conversation_member_user]
GO
ALTER TABLE [dbo].[message]  WITH CHECK ADD  CONSTRAINT [FK_message_conversation_member] FOREIGN KEY([conversation_id])
REFERENCES [dbo].[conversation_member] ([id])
GO
ALTER TABLE [dbo].[message] CHECK CONSTRAINT [FK_message_conversation_member]
GO
ALTER TABLE [dbo].[message]  WITH CHECK ADD  CONSTRAINT [FK_message_user] FOREIGN KEY([user_id])
REFERENCES [dbo].[user] ([id])
GO
ALTER TABLE [dbo].[message] CHECK CONSTRAINT [FK_message_user]
GO
ALTER TABLE [dbo].[conversation_member]  WITH CHECK ADD  CONSTRAINT [CK__conversati__role__45BE5BA9] CHECK  (([role]='member' OR [role]='admin'))
GO
ALTER TABLE [dbo].[conversation_member] CHECK CONSTRAINT [CK__conversati__role__45BE5BA9]
GO
USE [master]
GO
ALTER DATABASE [chatroom] SET  READ_WRITE 
GO

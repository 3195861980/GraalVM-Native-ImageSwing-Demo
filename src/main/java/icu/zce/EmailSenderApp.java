package icu.zce;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.Attachment;
import com.resend.services.emails.model.CreateEmailOptions;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class EmailSenderApp extends JFrame {
    private final JTextField fromField;
    private final JTextField toField;
    private final JTextField subjectField;
    private final JTextArea contentArea;
    private final DefaultListModel<File> attachmentListModel;
    private final Resend resend;

    public EmailSenderApp() {
        // 初始化 Resend 对象（需要替换为你的 API Key）
        resend = new Resend("key");

        // 设置窗口属性
        setTitle("邮件发送工具");
        setSize(600, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        try {
            // 加载网络图片作为程序图标
            URL iconUrl = new URL("https://avatars.githubusercontent.com/u/38883647?v=4");
            Image iconImage = ImageIO.read(iconUrl);
            setIconImage(iconImage);
        } catch (IOException e) {
            System.out.println("加载图片失败" + e.getMessage());
        }

        // 创建主面板和布局
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);

        // 添加发件人标签和输入框
        JLabel fromLabel = new JLabel("发件人:");
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.LINE_START;
        panel.add(fromLabel, c);

        fromField = new JTextField(30);
        c.gridx = 1;
        c.gridy = 0;
        panel.add(fromField, c);

        // 添加收件人标签和输入框
        JLabel toLabel = new JLabel("收件人:");
        c.gridx = 0;
        c.gridy = 1;
        panel.add(toLabel, c);

        toField = new JTextField(30);
        c.gridx = 1;
        c.gridy = 1;
        panel.add(toField, c);

        // 添加主题标签和输入框
        JLabel subjectLabel = new JLabel("主题:");
        c.gridx = 0;
        c.gridy = 2;
        panel.add(subjectLabel, c);

        subjectField = new JTextField(30);
        c.gridx = 1;
        c.gridy = 2;
        panel.add(subjectField, c);

        // 添加内容标签和文本域
        JLabel contentLabel = new JLabel("内容:");
        c.gridx = 0;
        c.gridy = 3;
        panel.add(contentLabel, c);

        contentArea = new JTextArea(10, 30);
        JScrollPane scrollPane = new JScrollPane(contentArea);
        c.gridx = 1;
        c.gridy = 3;
        panel.add(scrollPane, c);

        // 添加附件标签和列表
        JLabel attachmentLabel = new JLabel("附件:");
        c.gridx = 0;
        c.gridy = 4;
        panel.add(attachmentLabel, c);

        attachmentListModel = new DefaultListModel<>();
        JList<File> attachmentList = new JList<>(attachmentListModel);
        JScrollPane attachmentScrollPane = new JScrollPane(attachmentList);
        attachmentScrollPane.setPreferredSize(new Dimension(300, 100));
        c.gridx = 1;
        c.gridy = 4;
        panel.add(attachmentScrollPane, c);

        // 添加添加附件按钮
        JButton addAttachmentButton = getjButton();
        c.gridx = 1;
        c.gridy = 5;
        c.anchor = GridBagConstraints.LINE_START;
        panel.add(addAttachmentButton, c);

        // 添加删除附件按钮（右键删除选中的附件）
        attachmentList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e) && !attachmentList.isSelectionEmpty()) {
                    int selectedIndex = attachmentList.getSelectedIndex();
                    attachmentListModel.remove(selectedIndex);
                }
            }
        });

        // 添加发送按钮
        JButton sendButton = new JButton("发送");
        sendButton.addActionListener(e -> sendEmail());
        c.gridx = 1;
        c.gridy = 6;
        c.anchor = GridBagConstraints.LINE_END;
        panel.add(sendButton, c);

        // 将面板添加到窗口中
        add(panel);
    }


    private JButton getjButton() {
        JButton addAttachmentButton = new JButton("添加附件");
        addAttachmentButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setMultiSelectionEnabled(true);
            int option = fileChooser.showOpenDialog(EmailSenderApp.this);
            if (option == JFileChooser.APPROVE_OPTION) {
                File[] files = fileChooser.getSelectedFiles();
                for (File file : files) {
                    attachmentListModel.addElement(file);
                }
            }
        });
        return addAttachmentButton;
    }

    private void sendEmail() {
        String from = fromField.getText();
        String to = toField.getText();
        String subject = subjectField.getText();
        String content = contentArea.getText();

        List<Attachment> attachments = new ArrayList<>();
        for (int i = 0; i < attachmentListModel.size(); i++) {
            File file = attachmentListModel.getElementAt(i);
            try {
                String fileContent = Base64.getEncoder().encodeToString(Files.readAllBytes(file.toPath()));
                Attachment attachment = Attachment.builder()
                        .fileName(file.getName())
                        .content(fileContent)
                        .build();
                attachments.add(attachment);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "读取文件错误: " + file.getName(), "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        CreateEmailOptions emailOptions = CreateEmailOptions.builder()
                .from(from)
                .to(to.split(","))
                .subject(subject)
                .html(content)
                .attachments(attachments)
                .build();

        try {
            resend.emails().send(emailOptions);
            JOptionPane.showMessageDialog(this, "邮件发送成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
        } catch (ResendException ex) {
            JOptionPane.showMessageDialog(this, "发送邮件出错: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        // 设置界面语言为中文

            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        SwingUtilities.invokeLater(() -> new EmailSenderApp().setVisible(true));

        
    }
}

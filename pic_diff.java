/* �摜��r�������o�� */

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.TransferHandler;
import javax.swing.JFileChooser;

public class pic_diff extends JFrame implements ActionListener{
	/* �����o�ϐ� */
	// �萔
	static final int object_x = 80;
	static final int object_y = 24;
	static final int object_space = 10;
	// �ϐ�
	static int file_max;
	static pic_diff window;
	static JComboBox<String> combo_box1, combo_box2;
	static JTextField limen_text_field;
	JTextField[] file_text_field;
	static File[] file_name;
	static BufferedImage[] read_image;
	/* �R���X�g���N�^ */
	pic_diff(){
	// �E�B���h�E�̐ݒ�
		setTitle("�摜��r�������o��");
		getContentPane().setPreferredSize(new Dimension(position_x(4), position_y(file_max + 1)));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setVisible(true);
	// �I�u�W�F�N�g�̐ݒ�
		JPanel panel = new JPanel();
		panel.setLayout(null);
		// �摜���͗�
		file_text_field = new JTextField[file_max];
		for(int k = 0; k < file_max; k++){
			JLabel label = new JLabel("�摜" + (k + 1));
				label.setBounds(position_x(0), position_y(k), size_x(1), size_y(1));
				panel.add(label);
			file_text_field[k] = new JTextField();
				file_text_field[k].setBounds(position_x(1), position_y(k), size_x(2), size_y(1));
				file_text_field[k].setMargin(new Insets(0, 0, 0, 0));
				file_text_field[k].setEnabled(false);
				file_text_field[k].setTransferHandler(new DropFileHandler());
				panel.add(file_text_field[k]);
			JButton button = new JButton("�Q��...");
				button.setBounds(position_x(3), position_y(k), size_x(1), size_y(1));
				button.setMargin(new Insets(0, 0, 0, 0));
				button.addActionListener(this);
				button.setActionCommand("�Q��" + (k + 1));
				panel.add(button);
		}
		// ���̑�
		//���o���[�h
		final String[] extract_mode_str = new String[file_max + 1];
			extract_mode_str[0] = "����";
			for(int k = 0; k < file_max; k++) extract_mode_str[k + 1] = "����(" + (k + 1) + ")";
			combo_box1 = new JComboBox<String>(extract_mode_str);
			combo_box1.setBounds(position_x(0), position_y(file_max), size_x(1), size_y(1));
			combo_box1.addActionListener(this);
			combo_box1.setActionCommand("���o���[�h");
			panel.add(combo_box1);
		//����^�C�v
		final String[] judge_type_str = {"RGB", "Y"};
			combo_box2 = new JComboBox<String>(judge_type_str);
			combo_box2.setBounds(position_x(1), position_y(file_max), size_x(1), size_y(1));
			combo_box2.addActionListener(this);
			combo_box2.setActionCommand("����^�C�v");
			panel.add(combo_box2);
		//臒l
		limen_text_field = new JTextField("0");
			limen_text_field.setBounds(position_x(2), position_y(file_max), size_x(1), size_y(1));
			limen_text_field.setMargin(new Insets(0, 0, 0, 0));
			panel.add(limen_text_field);
		//�ۑ��{�^��
		JButton save_button = new JButton("�ۑ�");
			save_button.setBounds(position_x(3), position_y(file_max), size_x(1), size_y(1));
			save_button.setMargin(new Insets(0, 0, 0, 0));
			save_button.addActionListener(this);
			save_button.setActionCommand("�ۑ�");
			panel.add(save_button);
		getContentPane().add(panel, BorderLayout.CENTER);
		pack();
	}
	/* main�֐� */
	public static void main(String args[]){
		file_max = 2;
		if(args.length > 0){
			file_max = Integer.parseInt(args[0]);
			if(file_max < 2) file_max = 2;
		}
		pic_diff window = new pic_diff();
		file_name = new File[file_max];
		for(int k = 0; k < file_max; k++){
			file_name[k] = new File("");
		}
		read_image = new BufferedImage[file_max];
	}
	/* �C�x���g�����p */
	public void actionPerformed(ActionEvent event){
		// ���C���E�B���h�E�̓��͂ɑ΂��鏈��
		String command_str = event.getActionCommand();
		String extract_mode = (String)combo_box1.getSelectedItem();
		String judge_type   = (String)combo_box2.getSelectedItem();
		// �Q�ƃ{�^��
		if(command_str.substring(0, 2).equals("�Q��")){
			int file_index = Integer.parseInt(command_str.substring(2)) - 1;
			JFileChooser file_chooser = new JFileChooser();
			int selected = file_chooser.showOpenDialog(this);
			if (selected == JFileChooser.APPROVE_OPTION){
				file_name[file_index] = file_chooser.getSelectedFile();
				file_text_field[file_index].setText(file_name[file_index].toString());
				file_text_field[file_index].setToolTipText(file_name[file_index].toString());
			}
			return;
		}
		// �ۑ��{�^��
		if(command_str.equals("�ۑ�")){
			System.out.println(command_str + " " + extract_mode + " " + judge_type + " " + limen_text_field.getText());
			// �t�@�C����ǂݍ���
			int px = 0, py = 0;
			try{
				for(int k = 0; k < file_max; k++){
					file_name[k] = new File(file_text_field[k].getText());
					// �Ƃ肠�����ǂݍ���
					read_image[k] = ImageIO.read(file_name[k]);
					// �摜�T�C�Y���擾����
					int wx = read_image[k].getWidth(), wy = read_image[k].getHeight();
					if(k == 0){
						px = wx;
						py = wy;
					}else{
						if((px != wx) || (py != wy)){
							JOptionPane.showMessageDialog(this, "�قȂ�摜�T�C�Y�����݂��Ă��܂��I", "�摜��r�������o��", JOptionPane.ERROR_MESSAGE);
							return;
						}
					}
				}
			}catch(Exception error){
				JOptionPane.showMessageDialog(this, "�ǂݍ��߂Ȃ��摜������܂����I", "�摜��r�������o��", JOptionPane.ERROR_MESSAGE);
				return;
			}
			// �摜���r���A�����o���������s��
			BufferedImage save_Image = new BufferedImage(px, py, BufferedImage.TYPE_INT_ARGB);
			int limen = Integer.parseInt(limen_text_field.getText());
			if(limen < 0) limen = 0;
			for(int y = 0; y < py; y++){
				for(int x = 0; x < px; x++){
					// �e�摜�̊e��f�ɂ��āA�܂��͔���^�C�v�E臒l�ɂ��āu�������v�ƌ����邩�𔻒肷��
					boolean flg = true;
					int color = read_image[0].getRGB(x, y);
					int color_r = (color & 0xff0000) >> 16, color_g = (color & 0xff00) >> 8, color_b = color & 0xff;
					switch(judge_type){
					case "RGB":
						for(int k = 1; k < file_max; k++){
							int temp = read_image[k].getRGB(x, y);
							int temp_r = (temp & 0xff0000) >> 16, temp_g = (temp & 0xff00) >> 8, temp_b = temp & 0xff;
							int diff_r = color_r - temp_r, diff_g = color_g - temp_g, diff_b = color_b - temp_b;
							int diff = diff_r * diff_r + diff_g * diff_g + diff_b * diff_b;
							if(diff > limen){
								flg = false;
								break;
							}
						}
						break;
					case "Y":
						double color_y = 0.299 * color_r + 0.587 * color_g + 0.114 * color_b;
						for(int k = 1; k < file_max; k++){
							int temp = read_image[k].getRGB(x, y);
							int temp_r = (temp & 0xff0000) >> 16, temp_g = (temp & 0xff00) >> 8, temp_b = temp & 0xff;
							double temp_y = 0.299 * temp_r + 0.587 * temp_g + 0.114 * temp_b;
							double diff_y = color_y - temp_y;
							int diff = (int)(diff_y * diff_y);
							if(diff > limen){
								flg = false;
								break;
							}
						}
						break;
					}
					// ���茋�ʂɂ��A�ǂ����邩�𒊏o���[�h�ɂ���Ĕ��f����
					if(extract_mode.equals("����")){
						// ���ʕ����𒊏o����
						if(flg){
							// ���ʕ����ł���
							save_Image.setRGB(x, y, color);
						}else{
							// ���ʕ����łȂ�
							save_Image.setRGB(x, y, 0x00ffffff);
						}
					}else{
						// ���������𒊏o����
						if(flg){
							// ���ʕ����ł���
							save_Image.setRGB(x, y, 0x00ffffff);
						}else{
							// ���ʕ����łȂ�
							int p = combo_box1.getSelectedIndex() - 1;
							save_Image.setRGB(x, y, read_image[p].getRGB(x, y));
						}
					}
					
				}
			}
			// �������ʂ�ۑ�����
			JFileChooser file_chooser = new JFileChooser();
			int selected = file_chooser.showSaveDialog(this);
			if (selected == JFileChooser.APPROVE_OPTION){
				try{
					// �g���q�������t������
					String save_image_name = file_chooser.getSelectedFile().toString();
					String suffix = getSuffix(save_image_name);
					if(suffix.toLowerCase() != "png") save_image_name += ".png";
					ImageIO.write(save_Image, "png", new File(save_image_name));
				}catch(Exception error){
					JOptionPane.showMessageDialog(this, "�摜�ۑ��Ɏ��s���܂����I", "�摜��r�������o��", JOptionPane.ERROR_MESSAGE);
				}
			}
			return;
		}
	}
	/* �I�u�W�F�N�g�p�萔���v�Z���� */
	static int position_x(int x){
		return object_space * (x + 1) + object_x * x;
	}
	static int position_y(int y){
		return object_space * (y + 1) + object_y * y;
	}
	static int size_x(int x){
		return object_space * (x - 1) + object_x * x;
	}
	static int size_y(int y){
		return object_space * (y - 1) + object_y * y;
	}
	/* �t�@�C��������g���q���擾����
	 * http://chat-messenger.net/blog-entry-38.html
	 */
	public static String getSuffix(String fileName){
		if(fileName == null) return null;
		int point = fileName.lastIndexOf(".");
		if(point != -1) return fileName.substring(point + 1);
		return fileName;
	}
	/* �t�@�C���h���b�v���̐ݒ� */
	class DropFileHandler extends TransferHandler{
		@Override
		public boolean canImport(TransferSupport support){
			// �h���b�v����Ă��Ȃ��ꍇ�͎󂯎��Ȃ�
			if(!support.isDrop()) return false;
			// �h���b�v���ꂽ���̂��t�@�C���ł͂Ȃ��ꍇ�͎󂯎��Ȃ�
			if(!support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) return false;
			return true;
		}
		@Override
		@SuppressWarnings("unchecked")
		public boolean importData(TransferSupport support){
			// �󂯎���Ă������̂��m�F����
			if(!canImport(support)) return false;
			// �h���b�v����
			Transferable transferable = support.getTransferable();
			JTextField text_field = (JTextField)support.getComponent();
			try{
				// �t�@�C�����󂯎��
				List<File> files = (List<File>)transferable.getTransferData(DataFlavor.javaFileListFlavor);
				// �\������
				text_field.setText(files.get(0).toString());
				text_field.setToolTipText(files.get(0).toString());
			}catch(Exception error){
				error.printStackTrace();
			}
			return true;
		}
	}
}

/* 画像比較＆差分出力 */

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
	/* メンバ変数 */
	// 定数
	static final int object_x = 80;
	static final int object_y = 24;
	static final int object_space = 10;
	// 変数
	static int file_max;
	static pic_diff window;
	static JComboBox<String> combo_box1, combo_box2;
	static JTextField limen_text_field;
	JTextField[] file_text_field;
	static File[] file_name;
	static BufferedImage[] read_image;
	/* コンストラクタ */
	pic_diff(){
	// ウィンドウの設定
		setTitle("画像比較＆差分出力");
		getContentPane().setPreferredSize(new Dimension(position_x(4), position_y(file_max + 1)));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setVisible(true);
	// オブジェクトの設定
		JPanel panel = new JPanel();
		panel.setLayout(null);
		// 画像入力欄
		file_text_field = new JTextField[file_max];
		for(int k = 0; k < file_max; k++){
			JLabel label = new JLabel("画像" + (k + 1));
				label.setBounds(position_x(0), position_y(k), size_x(1), size_y(1));
				panel.add(label);
			file_text_field[k] = new JTextField();
				file_text_field[k].setBounds(position_x(1), position_y(k), size_x(2), size_y(1));
				file_text_field[k].setMargin(new Insets(0, 0, 0, 0));
				file_text_field[k].setEnabled(false);
				file_text_field[k].setTransferHandler(new DropFileHandler());
				panel.add(file_text_field[k]);
			JButton button = new JButton("参照...");
				button.setBounds(position_x(3), position_y(k), size_x(1), size_y(1));
				button.setMargin(new Insets(0, 0, 0, 0));
				button.addActionListener(this);
				button.setActionCommand("参照" + (k + 1));
				panel.add(button);
		}
		// その他
		//抽出モード
		final String[] extract_mode_str = new String[file_max + 1];
			extract_mode_str[0] = "共通";
			for(int k = 0; k < file_max; k++) extract_mode_str[k + 1] = "差分(" + (k + 1) + ")";
			combo_box1 = new JComboBox<String>(extract_mode_str);
			combo_box1.setBounds(position_x(0), position_y(file_max), size_x(1), size_y(1));
			combo_box1.addActionListener(this);
			combo_box1.setActionCommand("抽出モード");
			panel.add(combo_box1);
		//判定タイプ
		final String[] judge_type_str = {"RGB", "Y"};
			combo_box2 = new JComboBox<String>(judge_type_str);
			combo_box2.setBounds(position_x(1), position_y(file_max), size_x(1), size_y(1));
			combo_box2.addActionListener(this);
			combo_box2.setActionCommand("判定タイプ");
			panel.add(combo_box2);
		//閾値
		limen_text_field = new JTextField("0");
			limen_text_field.setBounds(position_x(2), position_y(file_max), size_x(1), size_y(1));
			limen_text_field.setMargin(new Insets(0, 0, 0, 0));
			panel.add(limen_text_field);
		//保存ボタン
		JButton save_button = new JButton("保存");
			save_button.setBounds(position_x(3), position_y(file_max), size_x(1), size_y(1));
			save_button.setMargin(new Insets(0, 0, 0, 0));
			save_button.addActionListener(this);
			save_button.setActionCommand("保存");
			panel.add(save_button);
		getContentPane().add(panel, BorderLayout.CENTER);
		pack();
	}
	/* main関数 */
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
	/* イベント処理用 */
	public void actionPerformed(ActionEvent event){
		// メインウィンドウの入力に対する処理
		String command_str = event.getActionCommand();
		String extract_mode = (String)combo_box1.getSelectedItem();
		String judge_type   = (String)combo_box2.getSelectedItem();
		// 参照ボタン
		if(command_str.substring(0, 2).equals("参照")){
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
		// 保存ボタン
		if(command_str.equals("保存")){
			System.out.println(command_str + " " + extract_mode + " " + judge_type + " " + limen_text_field.getText());
			// ファイルを読み込む
			int px = 0, py = 0;
			try{
				for(int k = 0; k < file_max; k++){
					file_name[k] = new File(file_text_field[k].getText());
					// とりあえず読み込む
					read_image[k] = ImageIO.read(file_name[k]);
					// 画像サイズを取得する
					int wx = read_image[k].getWidth(), wy = read_image[k].getHeight();
					if(k == 0){
						px = wx;
						py = wy;
					}else{
						if((px != wx) || (py != wy)){
							JOptionPane.showMessageDialog(this, "異なる画像サイズが混在しています！", "画像比較＆差分出力", JOptionPane.ERROR_MESSAGE);
							return;
						}
					}
				}
			}catch(Exception error){
				JOptionPane.showMessageDialog(this, "読み込めない画像がありました！", "画像比較＆差分出力", JOptionPane.ERROR_MESSAGE);
				return;
			}
			// 画像を比較しつつ、抜き出し処理を行う
			BufferedImage save_Image = new BufferedImage(px, py, BufferedImage.TYPE_INT_ARGB);
			int limen = Integer.parseInt(limen_text_field.getText());
			if(limen < 0) limen = 0;
			for(int y = 0; y < py; y++){
				for(int x = 0; x < px; x++){
					// 各画像の各画素について、まずは判定タイプ・閾値について「等しい」と言えるかを判定する
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
					// 判定結果により、どうするかを抽出モードによって判断する
					if(extract_mode.equals("共通")){
						// 共通部分を抽出する
						if(flg){
							// 共通部分である
							save_Image.setRGB(x, y, color);
						}else{
							// 共通部分でない
							save_Image.setRGB(x, y, 0x00ffffff);
						}
					}else{
						// 差分部分を抽出する
						if(flg){
							// 共通部分である
							save_Image.setRGB(x, y, 0x00ffffff);
						}else{
							// 共通部分でない
							int p = combo_box1.getSelectedIndex() - 1;
							save_Image.setRGB(x, y, read_image[p].getRGB(x, y));
						}
					}
					
				}
			}
			// 処理結果を保存する
			JFileChooser file_chooser = new JFileChooser();
			int selected = file_chooser.showSaveDialog(this);
			if (selected == JFileChooser.APPROVE_OPTION){
				try{
					// 拡張子を自動付加する
					String save_image_name = file_chooser.getSelectedFile().toString();
					String suffix = getSuffix(save_image_name);
					if(suffix.toLowerCase() != "png") save_image_name += ".png";
					ImageIO.write(save_Image, "png", new File(save_image_name));
				}catch(Exception error){
					JOptionPane.showMessageDialog(this, "画像保存に失敗しました！", "画像比較＆差分出力", JOptionPane.ERROR_MESSAGE);
				}
			}
			return;
		}
	}
	/* オブジェクト用定数を計算する */
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
	/* ファイル名から拡張子を取得する
	 * http://chat-messenger.net/blog-entry-38.html
	 */
	public static String getSuffix(String fileName){
		if(fileName == null) return null;
		int point = fileName.lastIndexOf(".");
		if(point != -1) return fileName.substring(point + 1);
		return fileName;
	}
	/* ファイルドロップ時の設定 */
	class DropFileHandler extends TransferHandler{
		@Override
		public boolean canImport(TransferSupport support){
			// ドロップされていない場合は受け取らない
			if(!support.isDrop()) return false;
			// ドロップされたものがファイルではない場合は受け取らない
			if(!support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) return false;
			return true;
		}
		@Override
		@SuppressWarnings("unchecked")
		public boolean importData(TransferSupport support){
			// 受け取っていいものか確認する
			if(!canImport(support)) return false;
			// ドロップ処理
			Transferable transferable = support.getTransferable();
			JTextField text_field = (JTextField)support.getComponent();
			try{
				// ファイルを受け取る
				List<File> files = (List<File>)transferable.getTransferData(DataFlavor.javaFileListFlavor);
				// 表示する
				text_field.setText(files.get(0).toString());
				text_field.setToolTipText(files.get(0).toString());
			}catch(Exception error){
				error.printStackTrace();
			}
			return true;
		}
	}
}

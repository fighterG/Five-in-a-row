package jsj.lgl;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.event.*;

public class ChessBoard extends JFrame implements ActionListener
{
	  //初始化默认人执黑先下，难度为简单
	private CBoardPanel cbBoard=new CBoardPanel(CBoardPanel.HUMAN_FIRST,CBoardPanel.EASY,this);
	
	private JMenu menu1 = new JMenu("游戏");
	private JMenu menu2 = new JMenu("设置");
	private JMenu menu3 = new JMenu("帮助");
	private JMenu menu1_1 = new JMenu("新游戏");
	private JMenu menu2_1 = new JMenu("等级");
	private JMenuItem item1_1_1 = new JMenuItem("玩家先");
	private JMenuItem item1_1_2 = new JMenuItem("电脑先");
	private JMenuItem item1_2 = new JMenuItem("悔棋");
	private JMenuItem item1_3 = new JMenuItem("退出");
    //private JMenuItem item1_4 = new JMenuItem("电脑荐棋");
	private JRadioButtonMenuItem item2_1_1 = new JRadioButtonMenuItem("低级");
	private JRadioButtonMenuItem item2_1_2 = new JRadioButtonMenuItem("高级");
	private JMenuItem item3_1=new JMenuItem("关于...");
	private JMenuBar bar=new JMenuBar();
	//类似于创建一个单选框，选中一个按钮时将关闭组的其他按钮
	private ButtonGroup group = new ButtonGroup();
	//设置棋盘界面
	public ChessBoard()
	{
		super("崔磊设计的人机五子棋");
		//为每一个按钮添加监听
		item1_1_1.addActionListener(this);
		item1_1_2.addActionListener(this);
		item1_2.addActionListener(this);
		item1_3.addActionListener(this);
		item2_1_1.addActionListener(this);
		item2_1_1.addActionListener(this);
		item3_1.addActionListener(this);
		item2_1_1.setSelected(true);
		group.add(item2_1_1);
		group.add(item2_1_2);
		
		menu1.add(menu1_1);
		menu1_1.add(item1_1_1);
		menu1_1.add(item1_1_2);
		
		menu1.add(item1_2);
		menu1.add(item1_3);	
       
        
		menu2.add(menu2_1);
		menu2_1.add(item2_1_1);
		menu2_1.add(item2_1_2);
		
		menu3.add(item3_1);
		
	
		bar.add(menu1);
		bar.add(menu2);
		bar.add(menu3);
		//Returns the menubar set on this frame.
		this.setJMenuBar(bar);
		
		add(cbBoard); // 加入棋盘面板
		
		this.setBounds(0,0,538,585);
		//不允许自由改变批棋盘大小
		this.setResizable(false);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public static void main(String args[])
	{
		new ChessBoard();
	}
      //重写各个按钮的监听事件
	@Override
	public void actionPerformed(ActionEvent e)
	{    
		//若当前事件是按下玩家先按钮调用restart()，玩家先走
		if(e.getSource() == item1_1_1) 
		{ 
			cbBoard.restart(CBoardPanel.HUMAN_FIRST, CBoardPanel.EASY, this);	
		}
		//若当前事件是按下电脑先按钮调用restart()，电脑先走
		else if(e.getSource() == item1_1_2)
		{
			cbBoard.restart(CBoardPanel.AI_FIRST, CBoardPanel.EASY, this);	
		}
		//若当前事件是按下悔棋按钮调用back()，双方各退一步棋
		else if(e.getSource() == item1_2)
		{
			cbBoard.back();
		}
		//若当前事件是按下退出按钮，退出界面程序完成
		else if(e.getSource() == item1_3)
		{
			System.exit(0);
		}
		//若当前事件是按下低级按钮调用setLevel()，难度为低级
		else if(e.getSource() == item2_1_1)
		{
			cbBoard.setLevel(CBoardPanel.EASY);	
		}
		//若当前事件是按下高级按钮调用setLevel()，难度为高级
		else if(e.getSource() == item2_1_2)
		{
			cbBoard.setLevel(CBoardPanel.HARD);
		}
		//若当前事件是按下关于按钮，弹出消息提示框
		else if(e.getSource() == item3_1)
		{
			JOptionPane.showMessageDialog(this,
					"由崔磊开发，算法参考了csdn上的大神，难度并不能选择只有初级难度，ai也不是很智能，希望以后能在完善一下",
					"关于",
					JOptionPane.INFORMATION_MESSAGE);
		}
	}
}

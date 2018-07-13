package jsj.lgl;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.event.*;

public class ChessBoard extends JFrame implements ActionListener
{
	  //��ʼ��Ĭ����ִ�����£��Ѷ�Ϊ��
	private CBoardPanel cbBoard=new CBoardPanel(CBoardPanel.HUMAN_FIRST,CBoardPanel.EASY,this);
	
	private JMenu menu1 = new JMenu("��Ϸ");
	private JMenu menu2 = new JMenu("����");
	private JMenu menu3 = new JMenu("����");
	private JMenu menu1_1 = new JMenu("����Ϸ");
	private JMenu menu2_1 = new JMenu("�ȼ�");
	private JMenuItem item1_1_1 = new JMenuItem("�����");
	private JMenuItem item1_1_2 = new JMenuItem("������");
	private JMenuItem item1_2 = new JMenuItem("����");
	private JMenuItem item1_3 = new JMenuItem("�˳�");
    //private JMenuItem item1_4 = new JMenuItem("���Լ���");
	private JRadioButtonMenuItem item2_1_1 = new JRadioButtonMenuItem("�ͼ�");
	private JRadioButtonMenuItem item2_1_2 = new JRadioButtonMenuItem("�߼�");
	private JMenuItem item3_1=new JMenuItem("����...");
	private JMenuBar bar=new JMenuBar();
	//�����ڴ���һ����ѡ��ѡ��һ����ťʱ���ر����������ť
	private ButtonGroup group = new ButtonGroup();
	//�������̽���
	public ChessBoard()
	{
		super("������Ƶ��˻�������");
		//Ϊÿһ����ť��Ӽ���
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
		
		add(cbBoard); // �����������
		
		this.setBounds(0,0,538,585);
		//���������ɸı������̴�С
		this.setResizable(false);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public static void main(String args[])
	{
		new ChessBoard();
	}
      //��д������ť�ļ����¼�
	@Override
	public void actionPerformed(ActionEvent e)
	{    
		//����ǰ�¼��ǰ�������Ȱ�ť����restart()���������
		if(e.getSource() == item1_1_1) 
		{ 
			cbBoard.restart(CBoardPanel.HUMAN_FIRST, CBoardPanel.EASY, this);	
		}
		//����ǰ�¼��ǰ��µ����Ȱ�ť����restart()����������
		else if(e.getSource() == item1_1_2)
		{
			cbBoard.restart(CBoardPanel.AI_FIRST, CBoardPanel.EASY, this);	
		}
		//����ǰ�¼��ǰ��»��尴ť����back()��˫������һ����
		else if(e.getSource() == item1_2)
		{
			cbBoard.back();
		}
		//����ǰ�¼��ǰ����˳���ť���˳�����������
		else if(e.getSource() == item1_3)
		{
			System.exit(0);
		}
		//����ǰ�¼��ǰ��µͼ���ť����setLevel()���Ѷ�Ϊ�ͼ�
		else if(e.getSource() == item2_1_1)
		{
			cbBoard.setLevel(CBoardPanel.EASY);	
		}
		//����ǰ�¼��ǰ��¸߼���ť����setLevel()���Ѷ�Ϊ�߼�
		else if(e.getSource() == item2_1_2)
		{
			cbBoard.setLevel(CBoardPanel.HARD);
		}
		//����ǰ�¼��ǰ��¹��ڰ�ť��������Ϣ��ʾ��
		else if(e.getSource() == item3_1)
		{
			JOptionPane.showMessageDialog(this,
					"�ɴ��ڿ������㷨�ο���csdn�ϵĴ����ѶȲ�����ѡ��ֻ�г����Ѷȣ�aiҲ���Ǻ����ܣ�ϣ���Ժ���������һ��",
					"����",
					JOptionPane.INFORMATION_MESSAGE);
		}
	}
}

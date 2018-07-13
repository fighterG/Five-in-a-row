package jsj.lgl;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class CBoardPanel extends JPanel implements MouseListener
{
	private static final int START_X=20;
	private static final int START_Y=20;
	//������֮��ļ��
	private static final int STEP=35;
	private static final int ROW=15;
	private static final int COL=15;
	
	public static final int BLANK=1;
	public static final int WHITE=-1;
	public static final int NONE=0;
	
	public static final int AI_FIRST=1;
	public static final int HUMAN_FIRST=2;
	
	public static final int EASY=2;
	public static final int HARD=4;	
	
	private BufferedImage chessBoardImage=null,whiteImage=null,blankImage=null;//ͼƬ
	private int[][] chesses=new int[ROW][COL];//�������
	private int aiColor,humColor;//���Ժ��˵�������ɫ
	private int level;//��ǰ�Ѷ�
	private ChessBoard cb;//���������
	private boolean isHumTurn=false;//�ǲ����ֵ����µĲ���ֵ��true�����ֵ�����
	private int currRow,currCol;//��ǰ���ӵ��к���
	private int winner=NONE;//��ʾӮ�ҵ���ɫ����ʼΪ��
	private ArrayList<Point> chessProcess=new ArrayList<Point>();//��ʾÿһ�������µ��Ⱥ�˳�����ڻ���
	//ֻҪһ���жԷ���һ�����ӻ����Ǳ߽�Ϳ�������
	private static final int FIVE = 10000;//�ܳ�����
	private static final int L_FOUR =2000;//�ܳɻ���
	private static final int D_FOUR = 1200;//�ܳ�����
	private static final int L_THREE = 800;//�ܳɻ���
	private static final int D_THREE = 180;//�ܳ�����
	private static final int L_TWO = 80;//�ܳɻ��
	private static final int D_TWO = 20;//�ܳ�����
	//x��
	private static final int X=1;
	//y��
	private static final int Y=2;
	//б45��
	private static final int M=3;
	//б135��
	private static final int N=4;
	
	public CBoardPanel(int whoFirst,int level,ChessBoard cb)//������
	{
		this.cb=cb;
		this.level=level;
		chesses=new int[ROW][COL];
		winner=NONE;
		if(whoFirst==HUMAN_FIRST)
		{
			isHumTurn=true;
			humColor=BLANK;
			aiColor=WHITE;
		}
		else
		{
			humColor=WHITE;
			aiColor=BLANK;
			isHumTurn=false;
			//������һ����
			aiGoOneStep();
		}						
		try
		{
			//��ͼƬ���ص�������,chessboardimageΪ���̱���ͼƬ
			chessBoardImage=ImageIO.read(new File("image/chessboard.jpg"));
			blankImage = ImageIO.read(new File("image/b.jpg"));
			whiteImage = ImageIO.read(new File("image/w.jpg"));
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		this.addMouseListener(this);
	}
	//���¿�ʼ�ķ�������Ӧ����Ϸ��ť��
	public void restart(int whoFirst,int level,ChessBoard cb)
	{
		this.level=level;
		chesses=new int[ROW][COL];
		winner=NONE;
		chessProcess.clear();
		if(whoFirst==HUMAN_FIRST)
		{
			isHumTurn=true;
			humColor=BLANK;
			aiColor=WHITE;
		}
		else
		{
			humColor=WHITE;
			aiColor=BLANK;
			isHumTurn=false;
			//������һ����
			aiGoOneStep();
		}
		this.repaint();
	}
	//��д��paintComponent�����������������ӵ���ɫ
	public void paintComponent(Graphics g)
	{
		super.paintComponents(g);
		
		g.drawImage(chessBoardImage,0,0,this);
		for(int i=0;i<ROW;i++)
		{
			for(int j=0;j<COL;j++)
			{
				if(chesses[i][j]==BLANK)
				{
					drawImageInRowCol(i,j,blankImage,g);
				}
				else if(chesses[i][j]==WHITE)
				{
					drawImageInRowCol(i,j,whiteImage,g);
				}
			}
		}
		
	}
	
	public void back()//��һ����
	{
		if(!isHumTurn||chessProcess.size()<2)
		{
			return;
		}
		winner=NONE;
		//��chessprocess������ɾ��������������������ֵ�ʹ������������һ��
		Point p=chessProcess.get(chessProcess.size()-1);
		chesses[p.x][p.y]=NONE;
		chessProcess.remove(chessProcess.size()-1);
		
		p=chessProcess.get(chessProcess.size()-1);
		chesses[p.x][p.y]=NONE;
		chessProcess.remove(chessProcess.size()-1);
		//����ǰ�������ӾͰ�����������һ����Ϊ���һ�����ӵ�����
		if(chessProcess.size()>0)
		{
			p=chessProcess.get(chessProcess.size()-1);
			currRow=p.x;currCol=p.y;
		}
		
		this.repaint();
	}
	
	public void setLevel(int level)//�������׳̶�
	{
		this.level=level;
	}
	//��ָ���к��л�����
	private void drawImageInRowCol(int row,int col,BufferedImage image,Graphics g)
	{
		int x=col*STEP+3;
		int y=row*STEP+3;
		g.drawImage(image,x,y,this);
	}
	
	private Point pressWhere(MouseEvent e)//�ж����ĵ��λ�ã�ת��Ϊ�к���
	{
		int x=e.getPoint().x;
		int y=e.getPoint().y;
		//���˸�STEP/2�������Ӷ�����֣��൱�ھ��ǲ�һ���ǵ�����꽻�沿��
		//���������Ĳ���Ҳ���Ǹ�λ�õ����ӡ�
		int row=(y-START_Y+STEP/2)/STEP;
		int col=(x-START_X+STEP/2)/STEP;
		return new Point(row,col);
	}
	
	private void goOneStep(int row,int col,int color)//����һ���壬���ж��Ƿ������ʤ��
	{
		chesses[row][col]=color;
		chessProcess.add(new Point(row,col));
		currRow=row;currCol=col;
		this.repaint();
		boolean flag=isWin(currRow,currCol,color);		
		if(flag)
		{
			winner=color;
			String msg;
			if(winner==BLANK)
			{
				msg="�ڷ�ʤ����";
			}
			else
			{
				msg="�׷�ʤ����";
			}
			JOptionPane.showMessageDialog(cb,msg,"��Ϸ����",JOptionPane.INFORMATION_MESSAGE);
		}
		//�����ǰ��ɫ����ҵ���û��ʤ�������������
		if(color==humColor&&winner==NONE)
		{
			aiGoOneStep();
		}
	}
	//�жϵ�ǰ��ָ�����������
	//���һ����4�����򣨺ᡢ������б����б���ķ����ͣ�
	//������ǰ����У��ĸ�λ�õĵ÷���ߡ�
	//������ҪӮ����ȻҪ�����������һ����ߣ���ô���ǾͿ��Լ���������ÿһ������������ߣ�һ�³�֮Ϊ��Ԫ�顣
	//һ�����������רҵ�����壩��������15*15�ġ���ôӦ����572����Ԫ�顣
	//ͬʱ�������Ԫ���к��ӺͰ��ӵ����������Բ��������λ�ã��Ĳ�ͬ��������Ԫ������ͬ�ķ֡�
	//Ȼ��ÿһ��λ�õĵ÷־��ǰ������λ�õ�������Ԫ��ĵ÷�֮�͡�
	private int score(int row,int col,int dir,int color)
	{
		 //���ж��Ƿ�Խ��
		if(row<0||row>=ROW||col<0||col>=COL||chesses[row][col]!=NONE)
		{
			return 0;
		}
		
		int x=0,y=0;
		boolean lflag=false,rflag=false;//���ұ�־λ�ж�����λ���Ƿ��������
		int qx=1;
		int ltemp=0,rtemp=0;
		//ͨ�������dir�ж����ͷ���
		switch(dir)
		{
		case X:x=1;y=0;
			break;
		case Y:x=0;y=1;
			break;
		case M:x=1;y=1;
			break;
		case N:x=1;y=-1;
			break;
		}
		int currRow=row,currCol=col;
		for(int i=1;i<=5;i++)//��Ӧ����벿������ķ������dir�ж�
		{
			currRow=row-i*y;
			currCol=col-i*x;
			//System.out.println("i1"+i);
			//System.out.println("1");
		    //int temp = 0;
			//�ж������Ƿ�Խ��
			if(currRow<0||currRow>=ROW||currCol<0||currCol>=COL)
			{
				//��lflagΪtrueʱ����ǰλ���޷�����
				lflag=true;
				break;
			}
			//��û��Խ��������жϣ��ж���һ�����Ӻʹ����������ɫ�Ƿ���һ����
			//����ɫһ�������ͼ�һ
			if(chesses[currRow][currCol]==color)
			{
				qx+=1;
			}
			//����һ�����Ӻʹ�������Ӳ�һ��������������û�б�Ҫ�ٽ����ж���
			else if(chesses[currRow][currCol]==-color)
			{
				lflag=true;
				break;
			}
			
			else if(chesses[currRow][currCol]==NONE)
			{
				for(int j=1;j<5;j++)
				{
					//currRow=row-i*y;
					//currCol=col-i*x;
					//System.out.println("i2"+i);
					if(chesses[currRow][currCol]==color)
					{
						ltemp++;
					}
					else if(chesses[currRow][currCol]==-color)
					{
						lflag=true;
						break;
					}
					else
					{
						break;
					}
				}
				break;
			}
		}
		//��Ӧ���Ұ벿������ķ������dir�ж�
		for(int i=1;i<=5;i++)
		{
			currRow=row+i*y;
			currCol=col+i*x;
			if(currRow<0||currRow>=ROW||currCol<0||currCol>=COL)
			{
				rflag=true;
				break;
			}
			if(chesses[currRow][currCol]==color)
			{
				qx+=1;
			}
			else if(chesses[currRow][currCol]==-color)
			{
				rflag=true;
				break;
			}
			else if(chesses[currRow][currCol]==NONE)
			{
				for(int j=1;j<5;j++)
				{
					//currRow=row+i*y;
					//currCol=col+i*x;
					if(chesses[currRow][currCol]==color)
					{
						rtemp++;
					}
					else if(chesses[currRow][currCol]==-color)
					{
						rflag=true;
						break;
					}
					else
					{
						break;
					}
				}
				break;
			}
		}
		int temp=Math.max(ltemp, rtemp);
  
		if(qx>=5)
		{
			return FIVE;
		}
		//������涼����ס���򷵻�0
		else if(lflag&&rflag)
		{
			return 0;
		}
		else
		{
			qx+=temp;
			//�����������޷�������,�൱����һ���Ƕ���ġ�������������
			if(lflag||rflag)
			{
				switch(qx)
				{
				case 1:return 0;
				case 2:return D_TWO;
				case 3:return D_THREE;
				case 4:return D_FOUR;
				}
			}else
			{
				
				//���������ҿ������壬�����м��п�λ�ã�Ҳ�����൱������
				if(temp>0)
				{
					
					switch(qx)
					{
					case 1:return 0;
					case 2:return D_TWO;
					case 3:return D_THREE;
					case 4:return D_FOUR;
					}
				}else
				{
					//���Ҷ��������壬���м�û�п�λΪ����
					switch(qx)
					{
					case 1:return 0;
					case 2:return L_TWO;
					case 3:return L_THREE;
					case 4:return L_FOUR;
					}				
				}
			}
		}
		return 0;
		
	}
	//���һ����4�����򣨺ᡢ������б����б���ķ����ͣ������ۼ�
	//����÷ָߵĵ��ǶԺ����������������������������õĶ�ɱ����ķ�����ͬʱ��ϰ���÷���ߵĵ�
	//��������λ�á�
	private int getScore(int row,int col,int color)
	{
		int result=0;
		int heng=score(row,col,X,color);int hengl=score(row,col,X,-color);
		int shu=score(row,col,Y,color);int shul=score(row,col,Y,-color);
		int xie45=score(row,col,M,color);int xie45l=score(row,col,M,-color);
		int xie135=score(row,col,N,color);int xie135l=score(row,col,N,-color);
		
		result=heng+shu+xie45+xie135+hengl+shul+xie45l+xie135l;
		//System.out.println(result);
		return result;
		
	}
	
	private boolean isWin(int row,int col,int color)//�жϵ�ǰ���Ƿ������������
	{
		
		int max=0;//����ͬɫ���ӵ������
		int temp=0;
		//�жϺ���
		for(int i=0;i<COL;i++)
		{
			if(chesses[row][i]==color)
			{
				temp++;
				if(max<temp)
				{
					max=temp;
				}
			}
			else
			{				
				temp=0;
			}
		}
		if(max>=5)
		{
			return true;
		}
		//�ж�����
		temp=0;max=0;
		for(int i=0;i<ROW;i++)
		{
			if(chesses[i][col]==color)
			{
				temp++;
				if(max<temp)
				{
					max=temp;
				}
			}
			else
			{
				temp=0;
			}
		}
		if(max>=5)
		{
			return true;
		}
		//�ж����·���
		int x=row,y=col;
		max=0;
		while(x>=0&&x<ROW&&y>=0&&y<COL&&chesses[x][y]==color)
		{
			x--;
			y--;
		}
		x++;y++;
		while(x>=0&&x<ROW&&y>=0&&y<COL&&chesses[x][y]==color)
		{
			max++;
			x++;
			y++;
		}
		if(max>=5)
		{
			return true;
		}
		//�ж����Ϸ���
		x=row;y=col;
		max=0;
		while(x>=0&&x<ROW&&y>=0&&y<COL&&chesses[x][y]==color)
		{
			x--;
			y++;
		}
		x++;y--;
		while(x>=0&&x<ROW&&y>=0&&y<COL&&chesses[x][y]==color)
		{
			max++;
			x++;
			y--;
		}
		if(max>=5)
		{
			return true;
		}
		return false;
	}
	
	//������һ����ķ���
	private void aiGoOneStep()
	{
		
		
		isHumTurn=false;
		int row=0;
		int col=0;
		if(chessProcess.size()==0)
		{
			//�������еĻ���һ���ߣ�7��7�����λ��
			row=7;col=7;
			goOneStep(row,col,aiColor);
			isHumTurn=true;
			return;
		}
		
		int score=0;
		//����������пհ׵㣬��ÿ����ķ�����Ȼ��ѡ�����нڵ��з�����ߵ�һ������
		for(int i=0;i<ROW;i++)
		{
			for(int j=0;j<COL;j++)
			{
				if(chesses[i][j]!=0)
				{
					
					continue;
				}
				int temp=getScore(i,j,aiColor);
				
				if(score<temp)
				{
					row=i;
					col=j;
					score=temp;
				}
			}
			//System.out.println();
		}
		System.out.println(score);
		goOneStep(row,col,aiColor);
		isHumTurn=true;
	}
	//��д�������¼�
	@Override
	public void mouseClicked(MouseEvent e)
	{
		
	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
		
	}

	@Override
	public void mouseExited(MouseEvent e)
	{
		
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		
	}
	//�����̧��ʱ�����������ָ��λ����һ����
	@Override
	public void mouseReleased(MouseEvent e)
	{
		if(!isHumTurn||winner!=NONE)
		{
			return;
		}
		Point p=pressWhere(e);
		if(chesses[p.x][p.y]!=0)
		{
			return;
		}
		isHumTurn=false;
		goOneStep(p.x,p.y,humColor);		
	}
	
	
}

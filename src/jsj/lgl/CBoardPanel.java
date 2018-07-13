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
	//行与行之间的间距
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
	
	private BufferedImage chessBoardImage=null,whiteImage=null,blankImage=null;//图片
	private int[][] chesses=new int[ROW][COL];//棋盘情况
	private int aiColor,humColor;//电脑和人的棋子颜色
	private int level;//当前难度
	private ChessBoard cb;//主类的引用
	private boolean isHumTurn=false;//是不是轮到人下的布尔值，true代表轮到人下
	private int currRow,currCol;//当前棋子的行和列
	private int winner=NONE;//表示赢家的颜色，初始为空
	private ArrayList<Point> chessProcess=new ArrayList<Point>();//表示每一个棋子下的先后顺序，用于悔棋
	//只要一端有对方的一个棋子或者是边界就看作死棋
	private static final int FIVE = 10000;//能成五连
	private static final int L_FOUR =2000;//能成活四
	private static final int D_FOUR = 1200;//能成死四
	private static final int L_THREE = 800;//能成活三
	private static final int D_THREE = 180;//能成死三
	private static final int L_TWO = 80;//能成活二
	private static final int D_TWO = 20;//能成死二
	//x轴
	private static final int X=1;
	//y轴
	private static final int Y=2;
	//斜45度
	private static final int M=3;
	//斜135度
	private static final int N=4;
	
	public CBoardPanel(int whoFirst,int level,ChessBoard cb)//构造器
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
			//电脑走一步棋
			aiGoOneStep();
		}						
		try
		{
			//把图片加载到框体中,chessboardimage为棋盘背景图片
			chessBoardImage=ImageIO.read(new File("image/chessboard.jpg"));
			blankImage = ImageIO.read(new File("image/b.jpg"));
			whiteImage = ImageIO.read(new File("image/w.jpg"));
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		this.addMouseListener(this);
	}
	//重新开始的方法（对应新游戏按钮）
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
			//电脑走一步棋
			aiGoOneStep();
		}
		this.repaint();
	}
	//重写的paintComponent方法，画棋盘上棋子的颜色
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
	
	public void back()//退一步棋
	{
		if(!isHumTurn||chessProcess.size()<2)
		{
			return;
		}
		winner=NONE;
		//从chessprocess数组中删除两个从下往上连续的值就代表黑棋白棋各退一步
		Point p=chessProcess.get(chessProcess.size()-1);
		chesses[p.x][p.y]=NONE;
		chessProcess.remove(chessProcess.size()-1);
		
		p=chessProcess.get(chessProcess.size()-1);
		chesses[p.x][p.y]=NONE;
		chessProcess.remove(chessProcess.size()-1);
		//若当前还有棋子就把数组中最后的一行作为最后一个棋子的坐标
		if(chessProcess.size()>0)
		{
			p=chessProcess.get(chessProcess.size()-1);
			currRow=p.x;currCol=p.y;
		}
		
		this.repaint();
	}
	
	public void setLevel(int level)//设置难易程度
	{
		this.level=level;
	}
	//在指定行和列画棋子
	private void drawImageInRowCol(int row,int col,BufferedImage image,Graphics g)
	{
		int x=col*STEP+3;
		int y=row*STEP+3;
		g.drawImage(image,x,y,this);
	}
	
	private Point pressWhere(MouseEvent e)//判断鼠标的点击位置，转换为行和列
	{
		int x=e.getPoint().x;
		int y=e.getPoint().y;
		//加了个STEP/2代表棋子多出部分，相当于就是不一定非点击坐标交叉部分
		//点击多出来的部分也算是该位置的棋子。
		int row=(y-START_Y+STEP/2)/STEP;
		int col=(x-START_X+STEP/2)/STEP;
		return new Point(row,col);
	}
	
	private void goOneStep(int row,int col,int color)//生成一步棋，并判断是否有玩家胜出
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
				msg="黑方胜啦！";
			}
			else
			{
				msg="白方胜啦！";
			}
			JOptionPane.showMessageDialog(cb,msg,"游戏结束",JOptionPane.INFORMATION_MESSAGE);
		}
		//如果当前颜色是玩家的且没有胜利，则电脑走棋
		if(color==humColor&&winner==NONE)
		{
			aiGoOneStep();
		}
	}
	//判断当前点指定方向的棋形
	//算出一个点4个方向（横、竖、左斜、右斜）的分数和，
	//评估当前棋局中，哪个位置的得分最高。
	//五子棋要赢，必然要有五个棋子在一起成线，那么我们就可以计算棋盘中每一个五格相连的线，一下称之为五元组。
	//一般情况（包括专业五子棋）下棋盘是15*15的。那么应该是572个五元组。
	//同时，针对五元组中黑子和白子的数量（可以不考虑相对位置）的不同，给该五元组评不同的分。
	//然后每一个位置的得分就是包含这个位置的所有五元组的得分之和。
	private int score(int row,int col,int dir,int color)
	{
		 //先判断是否越界
		if(row<0||row>=ROW||col<0||col>=COL||chesses[row][col]!=NONE)
		{
			return 0;
		}
		
		int x=0,y=0;
		boolean lflag=false,rflag=false;//左右标志位判断左右位置是否可以下棋
		int qx=1;
		int ltemp=0,rtemp=0;
		//通过传入的dir判断棋型方向
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
		for(int i=1;i<=5;i++)//对应的左半部，具体的方向传入的dir判断
		{
			currRow=row-i*y;
			currCol=col-i*x;
			//System.out.println("i1"+i);
			//System.out.println("1");
		    //int temp = 0;
			//判断左面是否越界
			if(currRow<0||currRow>=ROW||currCol<0||currCol>=COL)
			{
				//当lflag为true时代表当前位置无法下棋
				lflag=true;
				break;
			}
			//若没有越界则继续判断，判断下一个棋子和传入的棋子颜色是否是一样的
			//若颜色一样则棋型加一
			if(chesses[currRow][currCol]==color)
			{
				qx+=1;
			}
			//若下一个棋子和传入的棋子不一样，则该棋的左面没有必要再进行判断了
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
		//对应的右半部，具体的方向传入的dir判断
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
		//如果两面都被堵住，则返回0
		else if(lflag&&rflag)
		{
			return 0;
		}
		else
		{
			qx+=temp;
			//如果左或者右无法再下棋,相当于有一面是堵这的。把它看做死棋
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
				
				//如果左或者右可以下棋，但是中间有空位置，也可以相当于死棋
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
					//左右都可以下棋，且中间没有空位为活棋
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
	//算出一个点4个方向（横、竖、左斜、右斜）的分数和，进行累加
	//黑棋得分高的点是对黑棋有利，但白棋下在这里就是最好的扼杀黑棋的方法，同时结合白棋得分最高的点
	//计算出最佳位置。
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
	
	private boolean isWin(int row,int col,int color)//判断当前点是否有五连的情况
	{
		
		int max=0;//保存同色棋子的最大数
		int temp=0;
		//判断横向
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
		//判断纵向
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
		//判断右下方向
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
		//判断右上方向
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
	
	//电脑走一步棋的方法
	private void aiGoOneStep()
	{
		
		
		isHumTurn=false;
		int row=0;
		int col=0;
		if(chessProcess.size()==0)
		{
			//电脑先行的话第一步走（7，7）这个位置
			row=7;col=7;
			goOneStep(row,col,aiColor);
			isHumTurn=true;
			return;
		}
		
		int score=0;
		//程序遍历所有空白点，算每个点的分数，然后选择所有节点中分数最高的一个落子
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
	//重写鼠标监听事件
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
	//当鼠标抬起时触发，玩家在指定位置走一步棋
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

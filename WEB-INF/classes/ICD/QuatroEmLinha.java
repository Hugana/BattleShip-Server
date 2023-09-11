package ICD;


public class QuatroEmLinha {

	public static boolean play(char player, char[][] board, int escolha) {

		// variaveis auxiliares
		int linhas = board.length, colunas = board[0].length;
		int col;

		// pedir coluna
		// System.out.print("Escolha uma coluna (Jogador " + player + "): ");
		col = (escolha);

		// verificar se está dentro da board
		if (col < 1 || col > colunas) {
			System.out.println("Coluna fora dos limites: " + col);
			return false;
		}

		// basta verificar se a primeira linha tem espaço livre
		if (board[0][col - 1] == 0) {
			// percorrer linhas na coluna, começar do fim para encontrar a primeira vazia
			for (int i = linhas - 1; i >= 0; i--) {
				if (board[i][col - 1] == 0) {
					board[i][col - 1] = player;
					return true;
				}
			}

		} else {
			// System.out.println("Coluna cheia, escolha outra.");
			return false;

		}
		return true;

		// retorna coluna jogada

	}
	
	public static boolean lastPlayerWon(char[][] board, int col) {
		// variaveis auxiliares
		int linhas = board.length, colunas = board[0].length;
		int linha = 0, coluna = col - 1; // indices
		char lastPlayer = ' ';

		// saber ultimo jogador e linha
		for (int i = 0; i < linhas; i++) {
			// ao encontrar primeira peça não vazia na coluna recebida como argumento,
			// encontra-se a última jogada, obtendo a sua posição
			if (board[i][coluna] != 0) {
				linha = i;
				lastPlayer = board[linha][coluna];
				break;
			}
		}

		// verificação: percorrer a partir da localização da peça, ao encontrar peça
		// igual ctr aumenta, senão acaba o ciclo

		// VERTICAL: ver peças abaixo: só vale a pena se tiver pelo menos 4 peças
		// -> se linha menor que totalLinhas-3 não há linhas suficiente para vitória
		int ctrVer = 0;
		if (linha < (linhas - 3))
			for (int l = linha; l < board.length; l++)
				if (board[l][coluna] == lastPlayer)
					ctrVer++;
				else
					break;

		// HORIZONTAL: ver peças imediatamente laterais
		int ctrHor = 1; // 1 p contar c a propria
		// esquerda: -1 p ver a imediatamente ao lado
		for (int i = coluna - 1; i >= 0; i--)
			if (board[linha][i] == lastPlayer)
				ctrHor++;
			else
				break;

		// direita: +1 p ver a imediatamente ao lado
		for (int i = coluna + 1; i < colunas; i++)
			if (board[linha][i] == lastPlayer)
				ctrHor++;
			else
				break;

		// DIAGONAL
		int ctrDiagonalTopToBot = 1; // \ -> cima: x-- y-- baixo: x++ y++
		int ctrDiagonalBotToTop = 1; // / -> cima: x++ y-- baixo: x-- y++
		int auxlinha = linha, auxcoluna = coluna;

		// Top to Bottom: cima
		while (isInBoard(board, --auxlinha, --auxcoluna))
			if (board[auxlinha][auxcoluna] == lastPlayer)
				ctrDiagonalTopToBot++;
			else
				break;

		// repor valores auxiliares
		auxlinha = linha;
		auxcoluna = coluna;

		// Top to Bot: baixo
		while (isInBoard(board, ++auxlinha, ++auxcoluna))
			if (board[auxlinha][auxcoluna] == lastPlayer)
				ctrDiagonalTopToBot++;
			else
				break;

		// repor valores auxiliares
		auxlinha = linha;
		auxcoluna = coluna;

		// Bottom to Top: cima
		while (isInBoard(board, ++auxlinha, --auxcoluna))
			if (board[auxlinha][auxcoluna] == lastPlayer)
				ctrDiagonalBotToTop++;
			else
				break;

		// repor valores auxiliares
		auxlinha = linha;
		auxcoluna = coluna;

		// Bottom to Top: baixo
		while (isInBoard(board, --auxlinha, ++auxcoluna))
			if (board[auxlinha][auxcoluna] == lastPlayer)
				ctrDiagonalBotToTop++;
			else
				break;

		// se algum dos contadores for >= 4, há vitória
		if (ctrVer >= 4 || ctrHor >= 4 || ctrDiagonalBotToTop >= 4 || ctrDiagonalTopToBot >= 4)
			return true;

		// se chegar aqui não há vitória
		return false;
	}
	
	private static boolean isInBoard(char[][] board, int lin, int col) {

		// variaveis auxiliares
		int linhas = board.length;
		int colunas = board[0].length;

		// se indices recebidos estão dentro dos limites, retorna true
		// se pelo menos um dos indices estiver fora, retorna false
		return (lin >= 0) && (lin < linhas) && (col >= 0) && (col < colunas);
	}
	
	public static boolean existsFreePlaces(char[][] board) {

		// variaveis auxiliares
		int linhas = board.length, colunas = board[0].length;

		for (int i = 0; i < linhas; i++)
			for (int j = 0; j < colunas; j++)
				// se encontrar espaço vazio (0), retorno true
				if (board[i][j] == 0)
					return true;
		// se chegar aqui nao encontrei posicoes vazias
		return false;
	}
	
	
	
	public static String getBoardToString(char[][] board) {
		// variaveis auxiliares
				int linhas = board.length;
				int colunas = board[0].length;
				
				String paraReturn;
				//String linhaHorizontal = "+------------------------+";

				//System.out.println("|_1__2__3__4__5__6__7__8_|");
				
				paraReturn = "|_1__2__3__4__5__6__7__8_|\n";
				
				
				
//				String header = "|";
//				for (int i = 0; i < colunas; i++) {
//					header += "_" + (i + 1) + "_";
//				}
//				header += "|";
//				System.out.println(header);

				for (int i = 0; i < linhas; i++) {
					String linha = "| ";
					for (int j = 0; j < colunas; j++) {
						if (board[i][j] == 0) {
							linha += '0';
						} else {
							linha += board[i][j];
						}
						if (j == colunas - 1) {
							linha += " |";
						} else {
							linha += "  ";
						}
					}
					//System.out.println(linha);
					paraReturn += linha + "\n";
				}
				//System.out.println(linhaHorizontal);
				paraReturn += "+------------------------+\n";
				
				
				return paraReturn;
			}
	

	
	public static void showboard(char[][] board) {

		// variaveis auxiliares
		int linhas = board.length;
		int colunas = board[0].length;
		String linhaHorizontal = "+------------------------+";

		System.out.println("|_1__2__3__4__5__6__7__8_|");
//		String header = "|";
//		for (int i = 0; i < colunas; i++) {
//			header += "_" + (i + 1) + "_";
//		}
//		header += "|";
//		System.out.println(header);

		for (int i = 0; i < linhas; i++) {
			String linha = "| ";
			for (int j = 0; j < colunas; j++) {
				if (board[i][j] == 0) {
					linha += '0';
				} else {
					linha += board[i][j];
				}
				if (j == colunas - 1) {
					linha += " |";
				} else {
					linha += "  ";
				}
			}
			System.out.println(linha);
		}
		System.out.println(linhaHorizontal);
	}
	
	
	

}

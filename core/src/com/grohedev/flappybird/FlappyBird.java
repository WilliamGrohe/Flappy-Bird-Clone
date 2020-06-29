package com.grohedev.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Random;

import javafx.beans.binding.When;
import sun.rmi.runtime.Log;


public class FlappyBird extends ApplicationAdapter {

	private SpriteBatch batch;
	private Texture[] passaros;
	private Texture fundo;
	private Texture canoBaixo;
	private Texture canoTopo;
	private Texture gameOver;
	private Texture wgDev;
	private Random numeroRandomico;
	private BitmapFont fonte;
	private BitmapFont mensagem;
	private BitmapFont rodape;

	private BitmapFont score;

	private Circle circuloPassaro;
	private Rectangle retanguloCanoBaixo;
	private Rectangle retanguloCanoTopo;


	//atributos de Configuração
	private float larguraDispositivo;
	private float alturaDispositivo;
	private int estadoJogo = 0; // 0-> jogo parado | 1-> jogo iniciado | 2 -> game over
	private int pontuacao=0;
	public static int maiorPontuacao;

	private float variacao = 0;
	private float velocidadeQueda=0;
	private float posicaoInicialVertical;
	private float movimentoCanoHorizontal;
	private float espacoEntreCano;
	private float deltaTime;
	private float alturaCanoRandomica;
	private boolean marcouPonto=false;

	//config camera
	private OrthographicCamera camera;
	private Viewport viewPort;
	private final float VIRTUAL_WIDTH = 768;
	private final float VIRTUAL_HEIGHT = 1024;

	//database de pontuacao


	@Override
	public void create () {

		batch = new SpriteBatch();
		numeroRandomico = new Random();
		fonte = new BitmapFont();
		fonte.setColor(Color.WHITE);
		fonte.getData().setScale(6);

		mensagem = new BitmapFont();
		mensagem.setColor(Color.WHITE);
		mensagem.getData().setScale(3);

		rodape = new BitmapFont();
		rodape.setColor(Color.WHITE);
		rodape.getData().setScale(2);

		score = new BitmapFont();
		score.setColor(Color.WHITE);
		score.getData().setScale(3);

		passaros = new Texture[3];
		passaros[0] = new Texture("passaro1.png");
		passaros[1] = new Texture("passaro2.png");
		passaros[2] = new Texture("passaro3.png");

		fundo = new Texture("fundo.png");

		canoBaixo = new Texture("cano_baixo_maior.png");
		canoTopo = new Texture("cano_topo_maior.png");

        gameOver = new Texture("game_over.png");
        wgDev = new Texture("wg.png");

		/*********************************************
		 * CONFIGURAÇÕES DA CAMERA
		 */
		camera = new OrthographicCamera();
		camera.position.set(VIRTUAL_WIDTH/2, VIRTUAL_HEIGHT/2, 0);
		viewPort = new StretchViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);

		larguraDispositivo = VIRTUAL_WIDTH;
		alturaDispositivo = VIRTUAL_HEIGHT;
		posicaoInicialVertical = alturaDispositivo / 2;
		movimentoCanoHorizontal = larguraDispositivo;
		espacoEntreCano = 300;


	}

	@Override
	public void render () {
		camera.update();

		//limpar frames anteriores | otimiza mem
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);


		deltaTime = Gdx.graphics.getDeltaTime();
		variacao += deltaTime * 10;
		if (variacao > 3) variacao = 0;

		if ( estadoJogo == 0 ){

			if ( Gdx.input.justTouched() ){
				estadoJogo = 1;
			}

		}else {

			velocidadeQueda++;
			if (posicaoInicialVertical > 0 || velocidadeQueda < 0)
				posicaoInicialVertical = posicaoInicialVertical - velocidadeQueda;

			if ( estadoJogo == 1 ){

				movimentoCanoHorizontal -= deltaTime * 200;

				//pasaro
				if (Gdx.input.justTouched()) {
					velocidadeQueda = -13;
				}

				//movimento do cano
				if (movimentoCanoHorizontal < -canoTopo.getWidth()) {
					movimentoCanoHorizontal = larguraDispositivo;
					alturaCanoRandomica = numeroRandomico.nextInt(500) - 250;
					marcouPonto = false;
				}

				//verifica se o cano passou pelo passaro e marca o ponto
				if (movimentoCanoHorizontal < 120 ) {
					if (!marcouPonto) {
						pontuacao++;
						marcouPonto = true;
					}
				}

			}else{ // estado 2 -> game over

			    //verificar o toque na tela
                if ( Gdx.input.justTouched() ){

                    estadoJogo = 0;
                    pontuacao = 0;
                    velocidadeQueda = 0;
                    posicaoInicialVertical = alturaDispositivo / 2;
                    movimentoCanoHorizontal = larguraDispositivo;
                    marcouPonto = false;

                    if ( pontuacao >= maiorPontuacao ){
                        maiorPontuacao = pontuacao;
                    }

                }

            }
		}

		//confirar dados projeção da camera
		batch.setProjectionMatrix( camera.combined );

		batch.begin();

		batch.draw(fundo, 0,0, larguraDispositivo, alturaDispositivo);
		batch.draw(canoTopo, movimentoCanoHorizontal, alturaDispositivo / 2 + espacoEntreCano /2 + alturaCanoRandomica);
		batch.draw(canoBaixo, movimentoCanoHorizontal,alturaDispositivo / 2 - canoBaixo.getHeight() - espacoEntreCano / 2 + alturaCanoRandomica);
		batch.draw(wgDev, 2, 2);
		rodape.draw(batch, "Flappy Bird Clone V.1.0", 330, 40);
		batch.draw(passaros[ (int) variacao ],  120, posicaoInicialVertical);
		fonte.draw(batch, String.valueOf(pontuacao), larguraDispositivo /2, alturaDispositivo - 50);

		if ( estadoJogo == 2 ){
		    batch.draw(gameOver, larguraDispositivo / 2 - gameOver.getWidth() / 2, alturaDispositivo /2 );
		    mensagem.draw(batch, "Toque para reiniciar", larguraDispositivo / 2 - 200, alturaDispositivo /2 - gameOver.getHeight() / 2 );
        }

		score.draw(batch,String.valueOf(maiorPontuacao),larguraDispositivo /2 + 120, alturaDispositivo - 50 );

		batch.end();

		//criando as formas sob os elementos
		circuloPassaro = new Circle();
		circuloPassaro.set(120 + passaros[0].getWidth() / 2, posicaoInicialVertical + passaros[0].getHeight() / 2 , passaros[0].getWidth() / 2);

		retanguloCanoBaixo = new Rectangle(
				movimentoCanoHorizontal, alturaDispositivo / 2 - canoBaixo.getHeight() - espacoEntreCano / 2 + alturaCanoRandomica,
				canoBaixo.getWidth(), canoBaixo.getHeight()
		);

		retanguloCanoTopo = new Rectangle(
				movimentoCanoHorizontal, alturaDispositivo / 2 + espacoEntreCano /2 + alturaCanoRandomica,
				canoTopo.getWidth(), canoTopo.getHeight()
		);

		//teste de colisão
		if( Intersector.overlaps(circuloPassaro, retanguloCanoBaixo) || Intersector.overlaps(circuloPassaro, retanguloCanoTopo)
                || posicaoInicialVertical <= 0 || posicaoInicialVertical >= alturaDispositivo ){
			estadoJogo = 2;

		}

        if ( pontuacao > maiorPontuacao ){
            maiorPontuacao = pontuacao;
        }

	}

	@Override
	public void resize(int width, int height) {
		viewPort.update(width, height);
	}

	public static void setMaiorPontuacao(int maiorPontuacao) {
		FlappyBird.maiorPontuacao = maiorPontuacao;
	}

	public static int getMaiorPontuacao(int maiorPontuacao) {
		return FlappyBird.maiorPontuacao;
	}
}

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.api.objects.File;
import sun.security.krb5.Config;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

public class Bot extends TelegramLongPollingBot{

    String pathToSaveFolder = "D://TempFiles//";   //папка куда сохраняются файлы на сервер

    String botToken = "731541418:AAFHv_7fzbV05vlFy4BBwwzyE2VNhB0okQc";  //уникальный токен бота для идентификации
    String botName = "ScannerPrinter_bot";

    String noticePrint = "Отправлено на печать";
    String noticeHelp = "Загрузите файлы или наберите текст  для отправки на принтер"
                      + " или нажмите /сканировать для получения отсканированных файлов";
    String noticeScan = "Отсканировано";

    static int idTxtFile = 1;  //номер-имена создаваемых txt файлов (инкрементируется с каждым разом)

    public static void main(String[] args){

        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();

        try{
            telegramBotsApi.registerBot(new Bot());
        }catch (TelegramApiException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onUpdateReceived(Update update) {   //Пользователь отправляет сообщение
                                                    //бот принимает
        Message message = update.getMessage();

        if(message != null && message.hasText()){  //Если сообщение в сообщение есть текст

             switch (message.getText()){

                 case "/помощь":      sendMsg(message,noticeHelp); break;
                 case "/сканировать": scan(message);               break;
                 default:             print(message);              return;
             }
        }

        if(message != null && message.hasDocument()){  //Если в сообщении не было текста, но были файлы
            print(message);
        }
    }

    private void print(Message message){   //печатание

        if(message != null && message.hasDocument()){  //Если в сообщении есть файлы
            saveFile(message);
        }

        if(message != null && message.hasText()){  //Если в сообщении есть текст
             createTxtFile(message);
        }

        if(message != null && (message.hasDocument() || message.hasText())){
            sendMsg(message,noticePrint);
        }
    }

    private void scan(Message message){  //сканирование

    }

    public void saveFile(Message message){  //сохраняем файл

        System.out.print(message.getDocument().getMimeType());

        GetFile getFileRequest = new GetFile();
        getFileRequest.setFileId(message.getDocument().getFileId());

        try{

            org.telegram.telegrambots.meta.api.objects.File telegramFile = execute(getFileRequest);
            java.io.File upFile = downloadFile(telegramFile);
            java.io.File newFile = new java.io.File(pathToSaveFolder+message.getDocument().getFileName());
            upFile.renameTo(newFile);

        }catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private  void createTxtFile(Message message) {  //формируем .txt файл из текста

        while((new java.io.File(pathToSaveFolder+"//"+idTxtFile+".txt")).exists()){
            ++idTxtFile;
        }

        java.io.File file = new java.io.File(pathToSaveFolder+"//"+idTxtFile+".txt");

        try{

            file.createNewFile();
            FileWriter writer = new FileWriter(file);
            writer.write(message.getText());
            writer.flush();
            writer.close();
            ++idTxtFile;

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void sendMsg(Message message, String text){  //Вспомогательный метод
                                                        //для отправки сообщения пользователю
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setReplyToMessageId(message.getMessageId());
        sendMessage.setText(text);

        try{
            setButtons(sendMessage);
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public  void setButtons(SendMessage sendMessage){ //устанавливаем клавиатуру

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);  //Скрывать ли клавиатуру после использования

        //Создаем кнопки
        List<KeyboardRow> keyboars = new ArrayList<>();
        KeyboardRow keyboardFirstRow = new KeyboardRow();   //Первый ряд кнопок

        keyboardFirstRow.add(new KeyboardButton("/помощь"));
        keyboardFirstRow.add(new KeyboardButton("/сканировать"));

        keyboars.add(keyboardFirstRow);
        replyKeyboardMarkup.setKeyboard(keyboars);
    }

    @Override
    public String getBotUsername() {  return botName; }

    @Override
    public String getBotToken() {
        return botToken;
    }
}
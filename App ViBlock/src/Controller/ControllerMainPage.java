package Controller;

import Data.*;
import Model.*;
import Utils.StageManager;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;

public class ControllerMainPage {

    @FXML
    private Label user1Label;

    @FXML
    private Label user2Label;

    @FXML
    private Label dateLabel;

    @FXML
    private Label saldoAperturaLabel;

    @FXML
    private Label saldoGiorLabel;

    @FXML
    private Label totCassaLabel;

    @FXML
    private Label PersEntrateLabel;

    @FXML
    private Label prelievoLabel;

    @FXML
    private JFXTextField searchJFXTextField;

    @FXML
    private JFXButton searchClientJFXButton;

    @FXML
    private JFXButton tesseramentoJFXButton;

    @FXML
    private JFXButton ingressoProvaJFXButton;

    @FXML
    private JFXButton riepGiornataJFXButton;

    @FXML
    private JFXButton riepMensileJFXButton;

    @FXML
    private JFXButton prelievoJFXButton;

    @FXML
    private ImageView logoutImageView;

    @FXML
    private JFXButton esportaSaldoGiorJFXButton;

    private ArrayList<Manager> managers;

    private int dieciIngressi;
    private int mensile;
    private int trimestrale;
    private int tesseracorso;

    @FXML
    private void initialize()
    {
        //set a listener to check if qrcode is passed
        tesseramentoJFXButton.setOnAction(this::handleTesseramentoJFXButton);
        ingressoProvaJFXButton.setOnAction(this::handleIngressoProvaJFXButton);
        riepGiornataJFXButton.setOnAction(this::handleRiepGiornataJFXButton);
        riepMensileJFXButton.setOnAction(this::handleRiepMensileJFXButton);
        prelievoJFXButton.setOnAction(this::handlePrelievoJFXButton);
        esportaSaldoGiorJFXButton.setOnAction(this::handleEsportaSaldoGJFXButton);
        searchClientJFXButton.setOnAction(this::handleSearchClientJFXButton);
        logoutImageView.setOnMouseClicked(this::handleLogOutImageView);
        searchJFXTextField.setOnKeyReleased(event ->
        {
            if (event.getCode() == KeyCode.ENTER)
                searchClientJFXButton.fire();
        });

    }

    public void setManagers(ArrayList<Manager> managers) {
        this.managers = managers;
    }

    public void setSideScreen()
    {
        ControllerSideScreen controllerSideScreen = new ControllerSideScreen();
        controllerSideScreen.fillLabel(managers, user1Label, user2Label, dateLabel, saldoAperturaLabel, saldoGiorLabel, totCassaLabel, PersEntrateLabel, prelievoLabel);
    }


    private void handleLogOutImageView(MouseEvent mouseEvent) {
        managers.removeAll(managers);
        StageManager loginPageState = new StageManager();
        loginPageState.setStageHomepage((Stage) logoutImageView.getScene().getWindow());
    }

    private void handleSearchImageView(MouseEvent mouseEvent) {
    }

    private void handleEsportaSaldoGJFXButton(ActionEvent actionEvent) {
        /*first check if one balance for db is already into DB */
        ModelRiepilogoGiorn modelRiepilogoGiornDB = new ModelDBRiepilogoGiorn();
        int row = modelRiepilogoGiornDB.checkIfRecordExists();
        RiepilogoGiornaliero riepilogo = new RiepilogoGiornaliero();
        ControllerAlert confirm = new ControllerAlert();

        Date d = new Date();
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.ITALY);
        String finaleDate = dateFormat.format(d);
        riepilogo.setData(finaleDate);

        String[] s = totCassaLabel.getText().split(" ");
        riepilogo.setSaldoFinale(new BigDecimal(s[0]));
        riepilogo.setTesseramenti(modelRiepilogoGiornDB.getDailyMembership());
        BigDecimal membershipBalance = new BigDecimal(modelRiepilogoGiornDB.getDailyMembership()).multiply(new BigDecimal(20.00));
        riepilogo.setSoldiTesseramenti(membershipBalance);

        String[] s1 = saldoGiorLabel.getText().split(" ");
        String[] s2 = prelievoLabel.getText().split(" ");

        BigDecimal totDay = new BigDecimal(s1[0]).subtract(membershipBalance);
        if (totDay.intValue() < 0)
            riepilogo.setEntrateGiornata(new BigDecimal(0));
        else
            riepilogo.setEntrateGiornata(totDay);

        riepilogo.setPrelievi(new BigDecimal(s2[0]));

        if (row == 0)
        {
            /*Insert*/
            modelRiepilogoGiornDB.insertDayBalance(riepilogo);
        }
        else
        {
            /*Update*/
            modelRiepilogoGiornDB.updateDayBalance(riepilogo);
        }

        confirm.displayInformation("Esporta saldo giornaliero eseguito!");

    }

    private void handlePrelievoJFXButton(ActionEvent actionEvent) {

        ControllerAlert alert = new ControllerAlert();
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText("");
        dialog.setTitle("Prelievo");
        dialog.setContentText("Cifra prelevata:");
        ((Stage) dialog.getDialogPane().getScene().getWindow()).getIcons().add(new Image("images/Vi Block.png"));

        Optional<String> s = dialog.showAndWait();
        BigDecimal withdrawal = new BigDecimal(0.0);

        if (s.isPresent()) {
            try {
                int i = Integer.parseInt(s.get());
                withdrawal = new BigDecimal(i);
            } catch (NumberFormatException e) {
                alert.displayAlert("Controlla la cifra inserita!");
            }
        }

        if(!withdrawal.equals(new BigDecimal(0.0)))
        {
            ModelPrelievo modelPrelievoDB = new ModelDBPrelievo();
            modelPrelievoDB.insertWithdrawal(withdrawal, managers.get(0));
            setSideScreen();
        }


    }

    private void handleRiepMensileJFXButton(ActionEvent actionEvent) {
        ArrayList<RiepilogoGiornaliero> dailySummaries = new ArrayList<>();
        ModelRiepilogoGiorn modelRiepilogoGiornDB = new ModelDBRiepilogoGiorn();
        dailySummaries.addAll(modelRiepilogoGiornDB.getDailySummaries());

        StageManager monthSummaries = new StageManager();
        monthSummaries.setStageMonthSummary((Stage) riepMensileJFXButton.getScene().getWindow(), managers, dailySummaries);
    }

    private void handleRiepGiornataJFXButton(ActionEvent actionEvent) {
        /*fetch all information about the day*/
        ArrayList<Entrata> records = new ArrayList<>();
        ModelEntrata modelEntrataDB = new ModelDBEntrata();
        records.addAll(modelEntrataDB.getAllRecord());
        for (Entrata e: records) {
            if(e.getTipopagamento() == null || e.getTipopagamento().equalsIgnoreCase(""))
                e.setTipopagamento("Abbonamento");
            if(e.getImporto() == null)
                e.setImporto(new BigDecimal(0.00));
            if (e.getTesseramento() == null)
                e.setTesseramento("NO");
            if(e.getScarpette() == null)
                e.setScarpette("NO");
        }


        StageManager daySummary = new StageManager();
        daySummary.setStageDaySummary((Stage) riepGiornataJFXButton.getScene().getWindow(), managers, records);
    }

    private void handleIngressoProvaJFXButton(ActionEvent actionEvent) {
        StageManager tryEnter = new StageManager();
        tryEnter.setStageTryEnter((Stage) ingressoProvaJFXButton.getScene().getWindow(), managers);
    }

    private void handleTesseramentoJFXButton(ActionEvent actionEvent) {
        StageManager newMembership = new StageManager();
        newMembership.setStageNewMembership((Stage) tesseramentoJFXButton.getScene().getWindow(), managers);
    }

    private void handleSearchClientJFXButton(ActionEvent actionEvent) {
        ControllerAlert alert = new ControllerAlert();
        ModelCliente modelClienteDB = new ModelDBCliente();

        if (searchJFXTextField.getText().isEmpty())
            alert.displayAlert("Inserire un nome cognome o CF!\n");
        else
        {
            if(isCFcode(searchJFXTextField.getText()))
            {
                Person client = modelClienteDB.getClient(searchJFXTextField.getText());
                if(client == null)
                {
                    alert.displayAlert("Il cliente non è nel DB!\nEffettuare un ingresso prova o tesseramento.");
                    searchJFXTextField.clear();
                }

                else
                {
                    if(!ClientHasSubmission(searchJFXTextField.getText()))
                    {
                        alert.displayInformation("Il cliente non ha un abbonamento valido!\n");
                        /*Fetch client information*/
                        Person person = modelClienteDB.getClient(searchJFXTextField.getText());

                        /*Entrance page*/
                        StageManager entrancePage = new StageManager();
                        entrancePage.setStageEntrace((Stage) searchClientJFXButton.getScene().getWindow(), managers, person);
                    }
                }

            }
            else
            {
                /*take name and surname from textField*/
                String[] nameSurname = searchJFXTextField.getText().split(" ");
                /*if first name and last name are single, so name surname--> fetch client information*/
                if(nameSurname.length == 2)
                {
                    /*Fetch client information*/
                    Person client = modelClienteDB.getClientFromNameSurname(nameSurname[0], nameSurname[1]);
                    if(client == null)
                    {
                        alert.displayAlert("Il cliente non è nel DB!\nEffettuare un ingresso prova o tesseramento.");
                        searchJFXTextField.clear();
                    }
                    else
                    {
                        if(!ClientHasSubmission(client.getCf()))
                        {
                            alert.displayInformation("Il cliente non ha un abbonamento valido!\n");
                            /*Fetch client information*/
                            /*Entrance page*/
                            StageManager entrancePage = new StageManager();
                            entrancePage.setStageEntrace((Stage) searchClientJFXButton.getScene().getWindow(), managers, client);
                        }
                    }

                }
                else
                {
                    /*create a dialog to choose the right combination of name-surname*/
                    Alert alert1 = new Alert(Alert.AlertType.CONFIRMATION);
                    alert1.setTitle("Scegli la combinazione nome-cognome corretta");
                    alert1.setHeaderText("");
                    alert1.setContentText("Scegli il nome e cognome giusto:");
                    ((Stage)alert1.getDialogPane().getScene().getWindow()).getIcons().add(new Image("images/Vi Block.png"));

                    String name = searchJFXTextField.getText();
                    int firstSpace = name.indexOf(" "); // detect the first space character
                    String firstName2 = name.substring(0, firstSpace);  // get everything upto the first space character
                    String lastName2 = name.substring(firstSpace).trim(); // get everything after the first space, trimming the spaces off

                    String lastName = "";
                    String firstName = "";
                    if(name.split("\\w+").length>1){

                        lastName = name.substring(name.lastIndexOf(" ")+1);
                        firstName = name.substring(0, name.lastIndexOf(' '));
                    }
                    else{
                        firstName = name;
                    }

                    ButtonType buttonTypeOne = new ButtonType(String.format("Nome: %s - Cognome: %s",firstName2,lastName2));
                    ButtonType buttonTypeTwo = new ButtonType(String.format("Nome: %s - Cognome: %s",firstName, lastName));
                    ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

                    alert1.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo, buttonTypeCancel);
                    Optional<ButtonType> result = alert1.showAndWait();
                    if (result.get() == buttonTypeOne){
                        Person client = modelClienteDB.getClientFromNameSurname(firstName2,lastName2);
                        if(client == null)
                        {
                            alert.displayAlert("Il cliente non è nel DB!\nEffettuare un ingresso prova o tesseramento.");
                            searchJFXTextField.clear();
                        }
                        else
                        {
                            if(!ClientHasSubmission(client.getCf()))
                            {
                                alert.displayInformation("Il cliente non ha un abbonamento valido!\n");
                                /*Fetch client information*/
                                /*Entrance page*/
                                StageManager entrancePage = new StageManager();
                                entrancePage.setStageEntrace((Stage) searchClientJFXButton.getScene().getWindow(), managers, client);
                            }
                        }
                    } else if (result.get() == buttonTypeTwo) {
                        Person client = modelClienteDB.getClientFromNameSurname(firstName,lastName);
                        if(client == null)
                        {
                            alert.displayAlert("Il cliente non è nel DB!\nEffettuare un ingresso prova o tesseramento.");
                            searchJFXTextField.clear();
                        }
                        else
                        {
                            if(!ClientHasSubmission(client.getCf()))
                            {
                                alert.displayInformation("Il cliente non ha un abbonamento valido!\n");
                                /*Fetch client information*/
                                /*Entrance page*/
                                StageManager entrancePage = new StageManager();
                                entrancePage.setStageEntrace((Stage) searchClientJFXButton.getScene().getWindow(), managers, client);
                            }
                        }

                    }
                }
            }
        }

    }

    private boolean isCFcode(String cf){
        return cf.matches("[A-Z]{3}[A-Z]{3}[0-9]{2}[A-Z][0-9]{2}[A-Z][0-9]{3}[A-Z]");
    }

    private boolean ClientHasSubmission(String cf)
    {
        ModelCliente modelClienteDB = new ModelDBCliente();
        ControllerAlert alert = new ControllerAlert();
        /*first check if client has a valid subscription */
        dieciIngressi = modelClienteDB.checkTenEntranceSubmission(cf);
        mensile = modelClienteDB.checkMonthEntranceSubmission(cf);
        trimestrale = modelClienteDB.check3MonthEntranceSubmission(cf);
        tesseracorso = modelClienteDB.checkClassEntranceSubmission(cf);

        if(dieciIngressi > 0 || tesseracorso > 0)
        {
            modelClienteDB.updateClientSubmissionDayEntrance(cf);
            /*display summary entrance*/
            ModelDayEntrance modelDayEntranceDB = new ModelDBDayEntrance();
            DayEntrance dayEntrance = modelDayEntranceDB.getDayEntrance(cf);

            alert.displayInformation(String.format("Il cliente %s %s ha effettuato l'ingresso:\n%s\nRiepilogo ingressi residui: %d",dayEntrance.getName(),
                    dayEntrance.getSurname(), dayEntrance.getEntrance(),dayEntrance.getRemainingEntrance()));
            return true;
        }
        else if(mensile > 0 || trimestrale > 0)
        {
            modelClienteDB.updateClientSubmissionDurationEntrance(cf);

            /*display summary entrance*/
            ModelPeriodEntrance modelPeriodEntranceDB = new ModelDBPeriodEntrance();
            PeriodEntrance periodEntrance = modelPeriodEntranceDB.getPeriodEntrance(cf);

            alert.displayInformation(String.format("Il cliente %s %s ha effettuato l'ingresso:\n%s\nInzio abbonamento: %s\nFine abbonamento: %s",periodEntrance.getName(),
                    periodEntrance.getSurname(), periodEntrance.getEntrance(), periodEntrance.getStartSubmission(), periodEntrance.getEndSubmission()));
            return true;

        }
        return false;
    }
}


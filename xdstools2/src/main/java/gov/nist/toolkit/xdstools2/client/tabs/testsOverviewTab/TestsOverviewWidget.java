package gov.nist.toolkit.xdstools2.client.tabs.testsOverviewTab;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import gov.nist.toolkit.results.shared.Test;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.xdstools2.client.ToolkitService;
import gov.nist.toolkit.xdstools2.client.ToolkitServiceAsync;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by Diane Azais local on 10/11/2015.
 */
public class TestsOverviewWidget extends CellTable<Test> {

    ToolkitServiceAsync service = (ToolkitServiceAsync) GWT.create(ToolkitService.class);
    Logger LOGGER = Logger.getLogger("TestsOverviewWidget");

    TestsWidgetDataModel dataModel;
    TextColumn<Test> testnumberColumn, descriptionColumn, timeColumn, statusColumn;
    TestButtonsColumn<Test> buttonsColumn;



    public TestsOverviewWidget(){
        setDefaults();

        // ----- Create the data model -----
        dataModel = new TestsWidgetDataModel();


        // ----- Create the UI -----

        testnumberColumn = new TextColumn<Test>() {
            @Override
            public String getValue(Test object) {
                return object.getNumber();
            }
        };
        addColumn(testnumberColumn, "Test Number");

        descriptionColumn = new TextColumn<Test>() {
            @Override
            public String getValue(Test object) {
                return object.getDescription();
            }
        };
        addColumn(descriptionColumn, "Description");

        // Create custom TestButtonsCells
        buttonsColumn = new TestButtonsColumn<Test>() {
            @Override
            public String getValue(Test object) {
                return object.getCommands();
            }
        };
        buttonsColumn.setFieldUpdater(new FieldUpdater<Test, String>() {

            @Override
            /**
             * Return the element of the buttons cell that was clicked (icon or button).
             */
            public void update(int index, Test object, String value) {
                //Window.alert("This is the field updater, with value: " + value + " Index: " + index + " Test no: "+ object.getNumber());

                if (value == TestButtonsCell.PLAY_ICON_NAME){
                    runSingleTest(object.getNumber(), index);
                }
                else if (value == TestButtonsCell.REMOVE_ICON_NAME){
                    deleteSingleTestResults(object.getNumber());
                }
                else if (value == TestButtonsCell.TEST_PLAN_BUTTON_NAME){
                    //TODO retrieve test plan page based on Test or TestNumber and open link to that page
                    Window.open("link_to_HTML_page","_blank","");
                }
                else if (value == TestButtonsCell.LOG_BUTTON_NAME){
                    //TODO add link to log page
                    Window.open("link_to_HTML_page","_blank","");
                }
                else if (value == TestButtonsCell.TEST_DESCRIPTION_BUTTON_NAME){
                    //TODO add link to description page
                    Window.open("link_to_HTML_page","_blank","");
                }
            }
        });
        addColumn(buttonsColumn, "Commands");

        timeColumn = new TextColumn<Test>() {
            @Override
            public String getValue(Test object) {
                return object.getTime();
            }
        };
        addColumn(timeColumn, "Time");

        statusColumn = new TextColumn<Test>() {
            @Override
            public String getValue(Test object) {
                return object.getStatus();
            }
        };
        addColumn(statusColumn, "Status");


        // --------------------------------------------------------------
        // ----- Retrieve test results and set data into the widget -----
        // --------------------------------------------------------------

        AsyncCallback<List<Test>> testsListCallback = new AsyncCallback<List<Test>>()
        {
            @Override
            public void onFailure(Throwable caught)
            { LOGGER.severe("Failed to load the list of available tests for current site and session, in the Tests Overview tab.");
            }

            @Override
            public void onSuccess(List<Test> result) {
                ArrayList<Test> res = new ArrayList<Test>();
                res.addAll(result);
                dataModel.setData(res);
                refreshUIData() ;
            }};
            loadTestsData(testsListCallback);
        }

        /**
         * Load the full list of tests for a given Site and the current Session, as well as their parameters from the server
         * @param testsListCallback
         */
    private void loadTestsData(AsyncCallback<List<Test>> testsListCallback) {
        // TODO the currently selected Site must be retrieved and passed as argument
        service.getTestsList(new Site("testEHR"), testsListCallback);
    }


    // --------------------------------------------------------------
    // ------- Run a single test, retrieve and display results ------
    // --------------------------------------------------------------

    AsyncCallback<Test> runSingleTestCallback = new AsyncCallback<Test>()
    {
        @Override
        public void onFailure(Throwable caught)
        { LOGGER.severe("Failed to run a test for current site and session, in the Tests Overview tab.");
        }

        @Override
        public void onSuccess(Test result)
        { dataModel.updateSingleTestResult(result);
        refreshUIData();
        }
    };

    //TODO replace the hardcoded site name with the one retrieved from the UI
    private void runSingleTest(String testNumber, int index){
        service.runSingleTest(new Site("testEHR"), testNumber, runSingleTestCallback);
    }


    // --------------------------------------------------------------
    // ------ Delete logs for a single test and update display ------
    // --------------------------------------------------------------

    AsyncCallback<Test> deleteSingleLogCallback = new AsyncCallback<Test>()
    {
        @Override
        public void onFailure(Throwable caught)
        { LOGGER.severe("Failed to delete a test log, in the Tests Overview tab.");
        }

        @Override
        public void onSuccess(Test result)
        { dataModel.updateSingleTestResult(result);
            refreshUIData();
        }
    };

    //TODO replace the hardcoded site name with the one retrieved from the UI
    private void deleteSingleTestResults(String testNumber){
        service.deleteSingleTestResult(new Site("testEHR"), testNumber, deleteSingleLogCallback);
        }

    /**
     * Refreshes the data view (UI)
     */
    private void refreshUIData(){
        setRowData(0, dataModel.getData());
    }

    /**
     * Default options for the cell table
     */
    private void setDefaults(){
        setWidth("100%");
        setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
    }
}
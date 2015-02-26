package org.esa.beam.framework.gpf.operators.tooladapter;

import org.esa.beam.framework.gpf.*;
import org.esa.beam.framework.gpf.descriptor.OperatorDescriptor;
import org.esa.beam.util.logging.BeamLogManager;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Lucian Barbulescu.
 */
public class ToolAdapterOpSpi extends OperatorSpi {

    private static Logger logger;
    private File adapterFolder;

    static {
        logger = BeamLogManager.getSystemLogger();
        try {
            logger.fine("Scanning for registered tools.");
            List<File> moduleFolders = ToolAdapterIO.scanForModules(ToolAdapterIO.basePath + ToolAdapterConstants.TOOL_ADAPTER_REPO);
            moduleFolders.forEach(ToolAdapterOpSpi::registerModule);
        } catch (IOException e) {
            logger.severe("Failed scan for Tools descriptors: I/O problem: " + e.getMessage());
        }
    }

    /**
     * Register a tool as an operator.
     *
     * @param moduleFolder the folder of the tool adapter
     * @throws OperatorException in case of an error
     */
    public static void registerModule(File moduleFolder) throws OperatorException {
        OperatorSpi operatorSpi = ToolAdapterIO.readOperator(moduleFolder);
        String operatorName = operatorSpi.getOperatorDescriptor().getName() != null ? operatorSpi.getOperatorDescriptor().getName() : operatorSpi.getOperatorDescriptor().getAlias();
        OperatorSpiRegistry operatorSpiRegistry = GPF.getDefaultInstance().getOperatorSpiRegistry();
        if (operatorSpiRegistry.getOperatorSpi(operatorName) != null) {
            throw new OperatorException("Operator already registered");
        }
        operatorSpiRegistry.addOperatorSpi(operatorName, operatorSpi);
    }

    /**
     * Default constructor.
     */
    public ToolAdapterOpSpi() {
        super(ToolAdapterOp.class);
    }

    /**
     * Constructor.
     *
     * @param operatorDescriptor the operator adapterFolder to be used.
     */
    public ToolAdapterOpSpi(OperatorDescriptor operatorDescriptor) {
        super(operatorDescriptor);
    }

    public ToolAdapterOpSpi(OperatorDescriptor operatorDescriptor, File adapterFolder) {
        this(operatorDescriptor);
        this.adapterFolder = adapterFolder;
    }

    @Override
    public Operator createOperator() throws OperatorException {
        ToolAdapterOp toolOperator = (ToolAdapterOp) super.createOperator();
        toolOperator.setParameterDefaultValues();
        toolOperator.setAdapterFolder(this.adapterFolder);
        return toolOperator;
    }

}

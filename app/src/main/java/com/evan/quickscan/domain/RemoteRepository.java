package com.evan.quickscan.domain;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.textract.AmazonTextractClient;
import com.amazonaws.services.textract.model.DetectDocumentTextRequest;
import com.amazonaws.services.textract.model.DetectDocumentTextResult;
import com.amazonaws.services.textract.model.Document;
import com.amazonaws.util.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import javax.inject.Inject;

public class RemoteRepository {

    @Inject
    public RemoteRepository() {
    }

    public DetectDocumentTextResult callTextract(String accessKey, String secretKey, String imagePath)
            throws AmazonServiceException, IOException {
        BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);

        ClientConfiguration configuration = new ClientConfiguration();
        configuration.setConnectionTimeout(300000);
        configuration.setSocketTimeout(300000);

        AmazonTextractClient client = new AmazonTextractClient(credentials, configuration);
        InputStream inputStream = new FileInputStream(new File(imagePath));
        ByteBuffer imageBytes = ByteBuffer.wrap(IOUtils.toByteArray(inputStream));

        DetectDocumentTextRequest request = new DetectDocumentTextRequest()
                .withDocument(new Document().withBytes(imageBytes));

        return client.detectDocumentText(request);
    }
}

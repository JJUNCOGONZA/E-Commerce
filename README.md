# E-Commerce


### 1. App Planning (Architecture)

This high-level architecture diagram outlines the design for an e-commerce platform leveraging AWS services. The architecture includes user authentication, frontend and backend services, and automated deployment processes, utilizing a combination of traditional microservices and cloud resources to ensure scalability and reliability.

![Diagrama sin título](https://github.com/JJUNCOGONZA/E-Commerce/assets/47018595/b1339782-b66c-4469-a2b5-f375fadf3190)

- User: Interacts with the frontend application through the API Gateway.
- API Gateway: Routes requests to the backend services.
- Cognito: Handles user authentication and issues JWT tokens for secure access.
- ECS (Elastic Container Service):

    Login View: Manages user login processes.
    Product View: Handles product listings.

- GitHub Action: Automates deployments and updates to the ECS services.
- ECS:

    Product Service: Manages product data and interactions.
    Order Service: Handles order management processes.

- DynamoDB: Stores product and order data.

### 2. Cloud Design
![Captura desde 2024-06-19 22-58-52](https://github.com/JJUNCOGONZA/E-Commerce/assets/47018595/f9176bad-1d5f-432e-925b-4adcfb856201)


The project will use LocalStack to simulate a DynamoDB environment. Two microservices developed in Spring Boot and dockerized will be delivered. Below are the components and functionalities:

1. **Product Microservice:**
   - Implements full CRUD.
   - Connects to the DynamoDB database simulated in LocalStack.
   - Documented with Swagger.
   - Hexagonal architecture.

2. **Order Microservice:**
   - Performs the creation function detailed in step 4.

## Execution Instructions

### Prerequisites

- Docker and Docker Compose installed.
- LocalStack configured.
- AWS CLI installed and configured.

### AWS CLI Installation

- **On Ubuntu systems:**
  sudo apt update
  sudo apt install awscli -y

### Download the installer from AWS CLI MSI
Run the installer:
    msiexec.exe /i https://awscli.amazonaws.com/AWSCLIV2.msi

### Clone the Repository
    git clone https://github.com/JJUNCOGONZA/E-Commerce.git
    cd E-Commerce
Running LocalStack and Microservices

Use the following docker-compose.yml file to configure LocalStack and the microservices:

    version: '3.8'
    services:
      localstack:
        image: localstack/localstack
        container_name: localstack
        ports:
          - "4566:4566"
          - "4571:4571"
        environment:
          - SERVICES=s3,sqs,lambda,dynamodb,cognito,apigateway,iam,sts,cloudwatch
          - DEBUG=1
          - PERSISTENCE=1
          - LAMBDA_EXECUTOR=docker-reuse
        volumes:
          - ./localstack_data:/var/lib/localstack
          - /var/run/docker.sock:/var/run/docker.sock
    
        product-catalog:
          build: ./product-catalog
          depends_on:
          - localstack
        environment:
          - SPRING_PROFILES_ACTIVE=docker
        ports:
          - "8089:8089"

        order:
          build: ./order
          depends_on:
            - localstack
        environment:
            - SPRING_PROFILES_ACTIVE=docker
          ports:
          - "8090:8090"

      volumes:
        localstack_data:
    driver: local


### Deploy Microservices

      docker-compose up -d

### Creating Tables in DynamoDB

For the sake of simplicity, a LocalStack folder is added that includes scripts for creating the necessary tables. If needed, run the following scripts:

### Create the Products Table

    aws --endpoint-url=http://localhost:4566 dynamodb create-table \
    --table-name Products \
    --attribute-definitions AttributeName=Id,AttributeType=S AttributeName=Category,AttributeType=S \
    --key-schema AttributeName=Id,KeyType=HASH \
    --global-secondary-indexes \
        "IndexName=CategoryIndex,KeySchema=[{AttributeName=Category,KeyType=HASH}],Projection={ProjectionType=ALL},ProvisionedThroughput={ReadCapacityUnits=5,WriteCapacityUnits=5}" \
    --provisioned-throughput ReadCapacityUnits=5,WriteCapacityUnits=5

### Create the Orders Table

    aws --endpoint-url=http://localhost:4566 dynamodb create-table \
    --table-name Orders \
    --attribute-definitions \
        AttributeName=OrderId,AttributeType=S \
        AttributeName=ProductId,AttributeType=S \
    --key-schema \
        AttributeName=OrderId,KeyType=HASH \
        AttributeName=ProductId,KeyType=RANGE \
    --provisioned-throughput ReadCapacityUnits=5,WriteCapacityUnits=5

### List Tables to Verify Creation

    aws --endpoint-url=http://localhost:4566 dynamodb list-tables


### CI/CD Pipeline Configuration

The following GitHub Actions workflow sets up a basic CI/CD pipeline for the Product Catalog service. It includes steps for building, testing, version incrementing, changelog generation, Docker image building, and pushing to Docker Hub.


    name: CI/CD Pipeline

    # Triggers the workflow on push and pull request events targeting the main branch
    on:
      push:
          branches:
          - main
        pull_request:
          branches:
            - main

      jobs:
    build:
    runs-on: ubuntu-latest  # Specifies the runner to use

    steps:
      # Step to check out the repository's code
      - name: Checkout code
        uses: actions/checkout@v2

      # Step to set up JDK 17 using the adopt distribution
      - name: Set up JDK 17
        uses: actions/setup-java@v2
        with:
          distribution: 'adopt'
          java-version: '17'

      # Step to list the directory structure for debugging purposes
      - name: List directory structure
        run: ls -al

      # Step to build and test the Product Catalog Service
      - name: Build and Test Product Catalog Service
        run: |
          ls -al product-catalog
          cd product-catalog
          mvn clean install

      # Step to increment the version of the Product Catalog Service
      - name: Increment version for Product Catalog Service
        id: increment_version_product
        run: |
          cd product-catalog
          CURRENT_VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)
          NEW_VERSION=$(echo $CURRENT_VERSION | awk -F. -v OFS=. '{$NF++;print}')
          echo "New version: $NEW_VERSION"
          mvn versions:set -DnewVersion=$NEW_VERSION
          echo "new_version_product=$NEW_VERSION" >> $GITHUB_ENV

      # Step to commit and push the version increment for the Product Catalog Service
      - name: Commit and push version increment for Product Catalog Service
        env:
          GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
        run: |
          cd product-catalog
          git config --global user.name 'Jeisson Junco'
          git config --global user.email 'jeissonjuncogonzalez@gmail.com'
          git remote set-url origin https://${{ secrets.GITHUB_TOKEN }}@github.com/JJUNCOGONZA/E-Commerce
          if git diff-index --quiet HEAD --; then
            echo "No changes to commit"
          else
            git add pom.xml
            git commit -m "Increment version to ${{ env.new_version_product }}"
          fi
          git pull origin main --rebase
          git push origin HEAD:main

      # Step to create an initial tag if none exists
      - name: Create initial tag if none exists
        env:
          GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
        run: |
          if [ -z "$(git ls-remote --tags origin | grep refs/tags/v)" ]; then
            git tag v0.0.1
            git push origin v0.0.1
          fi

      # Step to generate the changelog for the Product Catalog Service
      - name: Generate changelog for Product Catalog Service
        id: generate_changelog_product
        uses: mikepenz/release-changelog-builder-action@v2
        with:
          configuration: .github/changelog-config.json
        env:
          GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}

      # Step to create a release for the Product Catalog Service
      - name: Create Release for Product Catalog Service
        uses: actions/create-release@v1
        env:
          GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
        with:
          tag_name: v${{ env.new_version_product }}
          release_name: Release ${{ env.new_version_product }}
          body: ${{ steps.generate_changelog_product.outputs.changelog }}
          draft: false
          prerelease: false

      # Step to build the Docker image for the Product Catalog Service
      - name: Build Docker image
        run: |
          cd product-catalog
          docker build -t ja5on96/product-catalog:${{ env.new_version_product }} .
          docker tag ja5on96/product-catalog:${{ env.new_version_product }} ja5on96/product-catalog:latest

      # Step to log in to Docker Hub
      - name: Login to Docker Hub
        env:
          DOCKER_USERNAME: ${{ secrets.DOCKER_USERNAME }}
          DOCKER_PASSWORD: ${{ secrets.DOCKER_PASSWORD }}
        run: |
          echo $DOCKER_PASSWORD | docker login -u $DOCKER_USERNAME --password-stdin

      # Step to debug Docker login by displaying Docker info
      - name: Debug Docker Login
        run: docker info

      # Step to push the Docker image to Docker Hub
      - name: Push Docker image
        env:
          DOCKER_USERNAME: ${{ secrets.DOCKER_USERNAME }}
          DOCKER_PASSWORD: ${{ secrets.DOCKER_PASSWORD }}
        run: |
          docker push ja5on96/product-catalog:${{ env.new_version_product }}
          docker push ja5on96/product-catalog:latest

![imagen](https://github.com/JJUNCOGONZA/E-Commerce/assets/47018595/4a3ecec4-63b1-4598-8b13-a353c4635055)


### 4. Backend

The source code for both services is available in the GitHub repository. Below are the key components and their implementation details.

### Product-catalog
http://localhost:8089/swagger-ui/index.html
### Order
http://localhost:8090/swagger-ui/index.html

![imagen](https://github.com/JJUNCOGONZA/E-Commerce/assets/47018595/0822c14a-81bb-410e-802e-3f262c3a8747)


### 5. Frontend

Unfortunately, due to time constraints, I was unable to develop a complete frontend interface. Instead, the focus was on ensuring the backend services were fully functional and well-documented. To facilitate interaction and visualization of the data, I used DynamoDB-Admin, which allows for a straightforward way to manage and view DynamoDB tables.

![imagen](https://github.com/JJUNCOGONZA/E-Commerce/assets/47018595/1996a169-4748-423d-9429-61f0dc1ec3bf)


Install DynamoDB-Admin:

    npm install -g dynamodb-admin

Run DynamoDB-Admin:

    dynamodb-admin


### Message of Appreciation

Thank you for the opportunity to present this test. I hope to be considered for this or other future opportunities.

Best regards,

Jeisson Junco

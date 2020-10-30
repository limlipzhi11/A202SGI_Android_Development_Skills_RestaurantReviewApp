<?php
// catalog/controller/api/project_login_api.php
class ControllerApiReviewApi extends Controller {
  //Function for customers, staff and owner to login
  public function login() {
    $user_data = array();
    $this->load->model('account/customer');
    $this->load->model('catalog/review_api');

    if (!$this->customer->login($this->request->post['email'], $this->request->post['password'])) {
      $user_data['auth']="fail";
      $user_data['error']="Invalid Login Credentials.";
    } else {
      $this->model_account_customer->deleteLoginAttempts($this->request->post['email']);
      $user_data['auth']="success";
      $user_data['customer_id']=$this->customer->getId();
      $tmp=$this->model_catalog_review_api->getUserData($user_data['customer_id']);
      $user_data['name']=$tmp['name'];
      $user_data['email']=$tmp['email'];
      $user_data['telephone']=$tmp['telephone'];
      
    }

    if (isset($this->request->server['HTTP_ORIGIN'])) {
      $this->response->addHeader('Access-Control-Allow-Origin: ' . $this->request->server['HTTP_ORIGIN']);
      $this->response->addHeader('Access-Control-Allow-Methods: GET, PUT, POST, DELETE, OPTIONS');
      $this->response->addHeader('Access-Control-Max-Age: 1000');
      $this->response->addHeader('Access-Control-Allow-Headers: Content-Type, Authorization, X-Requested-With');
    }
 
    $this->response->addHeader('Content-Type: application/json');
    $this->response->setOutput(json_encode($user_data));
  }

  //Function for customers to register a customer account
  public function register(){
    $user_data = array();
    if(isset($this->request->post['username'])&&isset($this->request->post['password'])&&isset($this->request->post['f_name'])&&isset($this->request->post['l_name'])&&isset($this->request->post['tel_no'])){
      $username=$this->request->post['username'];
      $password=$this->request->post['password'];
      $fname=$this->request->post['f_name'];
      $lname=$this->request->post['l_name'];
      $telno=$this->request->post['tel_no'];

      // load model
      $this->load->model('catalog/review_api');

      // get products
      $result = $this->model_catalog_review_api->register($username,$password,$fname,$lname,$telno);
      //print_r($result);
    }
    else{
      $result['reg']="fail";
      $result['error']="Unable to Register Account.";
    }
      
    if (isset($this->request->server['HTTP_ORIGIN'])) {
      $this->response->addHeader('Access-Control-Allow-Origin: ' . $this->request->server['HTTP_ORIGIN']);
      $this->response->addHeader('Access-Control-Allow-Methods: GET, PUT, POST, DELETE, OPTIONS');
      $this->response->addHeader('Access-Control-Max-Age: 1000');
      $this->response->addHeader('Access-Control-Allow-Headers: Content-Type, Authorization, X-Requested-With');
    }

    $this->response->addHeader('Content-Type: application/json');
    $this->response->setOutput(json_encode($result));
  }

  //Function to get all restaurants
  public function getAllRestaurants(){
    $restaurant_data = array();

    // load model
    $this->load->model('catalog/review_api');

    // get products
    $restaurant_data = $this->model_catalog_review_api->getAllRestaurants();
    //print_r($result);
      
    if (isset($this->request->server['HTTP_ORIGIN'])) {
      $this->response->addHeader('Access-Control-Allow-Origin: ' . $this->request->server['HTTP_ORIGIN']);
      $this->response->addHeader('Access-Control-Allow-Methods: GET, PUT, POST, DELETE, OPTIONS');
      $this->response->addHeader('Access-Control-Max-Age: 1000');
      $this->response->addHeader('Access-Control-Allow-Headers: Content-Type, Authorization, X-Requested-With');
    }

    $this->response->addHeader('Content-Type: application/json');
    $this->response->setOutput(json_encode($restaurant_data));
  }

  //Function to get featured restaurants
  public function getFeatured(){
    $restaurant_data = array();

    // load model
    $this->load->model('catalog/review_api');

    // get products
    $restaurant_data = $this->model_catalog_review_api->getFeatured();
    //print_r($result);
      
    if (isset($this->request->server['HTTP_ORIGIN'])) {
      $this->response->addHeader('Access-Control-Allow-Origin: ' . $this->request->server['HTTP_ORIGIN']);
      $this->response->addHeader('Access-Control-Allow-Methods: GET, PUT, POST, DELETE, OPTIONS');
      $this->response->addHeader('Access-Control-Max-Age: 1000');
      $this->response->addHeader('Access-Control-Allow-Headers: Content-Type, Authorization, X-Requested-With');
    }

    $this->response->addHeader('Content-Type: application/json');
    $this->response->setOutput(json_encode($restaurant_data));
  }

  //Function to get dishes
  public function getDishes(){
    $dish_data = array();

    if(isset($this->request->post['restaurant_id'])&&!empty($this->request->post['restaurant_id'])){
      // load model
      $this->load->model('catalog/review_api');

      // get products
      $dish_data = $this->model_catalog_review_api->getDishes($this->request->post['restaurant_id']);
      //print_r($result);
    }
      
    if (isset($this->request->server['HTTP_ORIGIN'])) {
      $this->response->addHeader('Access-Control-Allow-Origin: ' . $this->request->server['HTTP_ORIGIN']);
      $this->response->addHeader('Access-Control-Allow-Methods: GET, PUT, POST, DELETE, OPTIONS');
      $this->response->addHeader('Access-Control-Max-Age: 1000');
      $this->response->addHeader('Access-Control-Allow-Headers: Content-Type, Authorization, X-Requested-With');
    }

    $this->response->addHeader('Content-Type: application/json');
    $this->response->setOutput(json_encode($dish_data));
  }

  //Function to get restaurant rating info
  public function getRestaurantRating(){
    $rating_data = array();

    if(isset($this->request->post['restaurant_id'])&&!empty($this->request->post['restaurant_id'])){
      // load model
      $this->load->model('catalog/review_api');

      // get products
      $rating_data = $this->model_catalog_review_api->getRestaurantRating($this->request->post['restaurant_id']);
      //print_r($result);
    }
      
    if (isset($this->request->server['HTTP_ORIGIN'])) {
      $this->response->addHeader('Access-Control-Allow-Origin: ' . $this->request->server['HTTP_ORIGIN']);
      $this->response->addHeader('Access-Control-Allow-Methods: GET, PUT, POST, DELETE, OPTIONS');
      $this->response->addHeader('Access-Control-Max-Age: 1000');
      $this->response->addHeader('Access-Control-Allow-Headers: Content-Type, Authorization, X-Requested-With');
    }

    $this->response->addHeader('Content-Type: application/json');
    $this->response->setOutput(json_encode($rating_data));
  }

  //Function to give restaurant a review
  public function giveRestaurantReview(){
    $rating_data = array();

    if(isset($this->request->post['restaurant_id'])&&!empty($this->request->post['restaurant_id'])&&isset($this->request->post['customer_id'])&&!empty($this->request->post['customer_id'])&&isset($this->request->post['rating'])&&!empty($this->request->post['rating'])&&isset($this->request->post['review'])&&!empty($this->request->post['review'])){
      // load model
      $this->load->model('catalog/review_api');

      // get products
      $rating_data = $this->model_catalog_review_api->giveRestaurantReview($this->request->post['restaurant_id'],$this->request->post['customer_id'],$this->request->post['rating'],$this->request->post['review']);
      //print_r($result);
    }
    else{
      $rating_data['status']="fail";
      $rating_data['error']="Access Denied";
    }
      
    if (isset($this->request->server['HTTP_ORIGIN'])) {
      $this->response->addHeader('Access-Control-Allow-Origin: ' . $this->request->server['HTTP_ORIGIN']);
      $this->response->addHeader('Access-Control-Allow-Methods: GET, PUT, POST, DELETE, OPTIONS');
      $this->response->addHeader('Access-Control-Max-Age: 1000');
      $this->response->addHeader('Access-Control-Allow-Headers: Content-Type, Authorization, X-Requested-With');
    }

    $this->response->addHeader('Content-Type: application/json');
    $this->response->setOutput(json_encode($rating_data));
  }

  //Function to get a restaurants review
  public function getRestaurantReview(){
    $review_data = array();

    if(isset($this->request->post['restaurant_id'])&&!empty($this->request->post['restaurant_id'])){
      // load model
      $this->load->model('catalog/review_api');

      // get products
      $review_data = $this->model_catalog_review_api->getRestaurantReview($this->request->post['restaurant_id']);
      //print_r($result);
    }
    else{
      $review_data['status']="fail";
      $review_data['error']="Access Denied";
    }
      
    if (isset($this->request->server['HTTP_ORIGIN'])) {
      $this->response->addHeader('Access-Control-Allow-Origin: ' . $this->request->server['HTTP_ORIGIN']);
      $this->response->addHeader('Access-Control-Allow-Methods: GET, PUT, POST, DELETE, OPTIONS');
      $this->response->addHeader('Access-Control-Max-Age: 1000');
      $this->response->addHeader('Access-Control-Allow-Headers: Content-Type, Authorization, X-Requested-With');
    }

    $this->response->addHeader('Content-Type: application/json');
    $this->response->setOutput(json_encode($review_data));
  }

  //Function to give dish a review
  public function giveDishReview(){
    $rating_data = array();

    if(isset($this->request->post['dish_id'])&&!empty($this->request->post['dish_id'])&&isset($this->request->post['customer_id'])&&!empty($this->request->post['customer_id'])&&isset($this->request->post['rating'])&&!empty($this->request->post['rating'])&&isset($this->request->post['review'])&&!empty($this->request->post['review'])){
      // load model
      $this->load->model('catalog/review_api');

      // get products
      $rating_data = $this->model_catalog_review_api->giveDishReview($this->request->post['dish_id'],$this->request->post['customer_id'],$this->request->post['rating'],$this->request->post['review']);
      //print_r($result);
    }
    else{
      $rating_data['status']="fail";
      $rating_data['error']="Access Denied";
    }
      
    if (isset($this->request->server['HTTP_ORIGIN'])) {
      $this->response->addHeader('Access-Control-Allow-Origin: ' . $this->request->server['HTTP_ORIGIN']);
      $this->response->addHeader('Access-Control-Allow-Methods: GET, PUT, POST, DELETE, OPTIONS');
      $this->response->addHeader('Access-Control-Max-Age: 1000');
      $this->response->addHeader('Access-Control-Allow-Headers: Content-Type, Authorization, X-Requested-With');
    }

    $this->response->addHeader('Content-Type: application/json');
    $this->response->setOutput(json_encode($rating_data));
  }

  //Function to get a dish's review
  public function getDishReview(){
    $review_data = array();

    if(isset($this->request->post['dish_id'])&&!empty($this->request->post['dish_id'])){
      // load model
      $this->load->model('catalog/review_api');

      // get products
      $review_data = $this->model_catalog_review_api->getDishReview($this->request->post['dish_id']);
      //print_r($result);
    }
    else{
      $review_data['status']="fail";
      $review_data['error']="Access Denied";
    }
      
    if (isset($this->request->server['HTTP_ORIGIN'])) {
      $this->response->addHeader('Access-Control-Allow-Origin: ' . $this->request->server['HTTP_ORIGIN']);
      $this->response->addHeader('Access-Control-Allow-Methods: GET, PUT, POST, DELETE, OPTIONS');
      $this->response->addHeader('Access-Control-Max-Age: 1000');
      $this->response->addHeader('Access-Control-Allow-Headers: Content-Type, Authorization, X-Requested-With');
    }

    $this->response->addHeader('Content-Type: application/json');
    $this->response->setOutput(json_encode($review_data));
  }

  //Function to get a dish's rating
  public function getDishRating(){
    $review_data = array();

    if(isset($this->request->post['dish_id'])&&!empty($this->request->post['dish_id'])){
      // load model
      $this->load->model('catalog/review_api');

      // get products
      $review_data = $this->model_catalog_review_api->getDishRating($this->request->post['dish_id']);
      //print_r($result);
    }
    else{
      $review_data['status']="fail";
      $review_data['error']="Access Denied";
    }
      
    if (isset($this->request->server['HTTP_ORIGIN'])) {
      $this->response->addHeader('Access-Control-Allow-Origin: ' . $this->request->server['HTTP_ORIGIN']);
      $this->response->addHeader('Access-Control-Allow-Methods: GET, PUT, POST, DELETE, OPTIONS');
      $this->response->addHeader('Access-Control-Max-Age: 1000');
      $this->response->addHeader('Access-Control-Allow-Headers: Content-Type, Authorization, X-Requested-With');
    }

    $this->response->addHeader('Content-Type: application/json');
    $this->response->setOutput(json_encode($review_data));
  }
}
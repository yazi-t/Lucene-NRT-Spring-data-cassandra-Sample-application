<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<t:wrapper>
    <jsp:attribute name="page_before">
        <link rel="stylesheet" type="text/css" href="<c:url value="/css/slick.min.css" />" />
<link rel="stylesheet" type="text/css" href="<c:url value="/css/slick-theme.min.css" />" />
    </jsp:attribute>

    <jsp:attribute name="page_heading">

    My Ad Web
  </jsp:attribute>

    <jsp:attribute name="page_body">
        <div id="container"></div>
        <script src="<c:url value="/js/react.js" />"></script>
        <script src="<c:url value="/js/react-dom.js" />"></script>
        <script src="<c:url value="/js/browser.min.js"/>"></script>
        <script src="<c:url value="/js/dropzone.js"/>"></script>
        <script src="<c:url value="/js/axios.js"/>"></script>
<script type="text/babel">

    var myDropzone;

  class Create extends React.Component {
    constructor(props) {
      super(props);
      this.state = {
		value: '',
		adCategories: [],
		locations: [],
		salesAreas:[]
	  };

      this.handleChange = this.handleChange.bind(this);
      this.handleSubmit = this.handleSubmit.bind(this);
	  
	  this.getAllAdCategories = this.getAllAdCategories.bind(this);
	  this.getAllLocations = this.getAllLocations.bind(this);
	  this.getAllSalesAreas = this.getAllSalesAreas.bind(this);
    }
	
	componentDidMount() {
        this.getAllAdCategories();
		this.getAllLocations();
		this.getAllSalesAreas();
        myDropzone = new Dropzone("div#myId", { url: "<c:url value="/add-image"/>"});
    }

    handleChange(event) {
      const target = event.target;
      const value = target.type === 'checkbox' ? target.checked : target.value;
      const name = target.name;

      this.setState({
        [name]: value
      });

      if (target.type === 'select-one') {
          const text = target.options[target.selectedIndex].text;
          const name_text = name + '_text';

          this.setState({
              [name_text]: text
          });
      }
    }

    clearForm() {
        this.createForm.reset();

        this.setState({ adTitle: '' });
        this.setState({ adText: '' });
        this.setState({ adLocation: '' });
        this.setState({ adLocation_text: '' });
        this.setState({ adCategory: '' });
        this.setState({ adCategory_text: '' });
        this.setState({ salesArea: '' });
        this.setState({ adTitle: '' });
        this.setState({ salesArea_text: '' });
        this.setState({ price: '' });

        myDropzone.removeAllFiles();
    }

    handleSubmit(event) {
      // alert('A name was submitted: TITLE: ' + this.state.adTitle + ', TEXT: ' + this.state.adText + ', LOCATION: ' + this.adLocation + ', AD_CAT: '  + this.adCategory + ', SALES_A: ' + this.salesArea);
        axios.post('<c:url value="/create" />',
            {
                title: this.state.adTitle,
                body: this.state.adText,
                locationId: this.state.adLocation,
                adCategoryId: this.state.adCategory,
                salesArea: this.state.salesArea,
                price: this.state.price
            })
            .then((result) => {
            console.log(result);
                if (result.data.type === 'SUCCESS') {
                    alert("Saved. \n message: " + result.data.message);
                    this.clearForm();
                } else
                    alert("Failed saving. \n message: " + result.data.message);
            });
      event.preventDefault();
    }
	
	getAllAdCategories(){
        fetch('<c:url value="/ad-categories"/>')
            .then((res) => {
                return res.json();
            })
            .then((res) => {
                this.setState({ adCategories: res });
            });
    }
                
	getAllLocations(){
        fetch('<c:url value="/locations"/>')
            .then((res) => {
                return res.json();
            })
            .then((res) => {
                this.setState({ locations: res });
			});
	}
	
	getAllSalesAreas(){
        fetch('<c:url value="/sales-areas"/>')
            .then((res) => {
                return res.json();
            })
            .then((res) => {
                this.setState({ salesAreas: res });
			});
	}

    render() {
      return (
		
		
		<div className="row">
			<div className="col-md-6">
				<h2>Enter Details</h2>
				<form ref={(el) => this.createForm = el} onSubmit={this.handleSubmit} className="form-horizontal">
					<div className="form-group">
						<label>Title</label>
						<input name="adTitle" type="text" className="form-control" onChange={this.handleChange} />
					</div>

					<div className="form-group">
						<label>Description</label>
						<textarea name="adText" className="form-control" onChange={this.handleChange} />
					</div>
					
					<div className="form-group">
						<label>Ad Category</label>
						<select name="adCategory" className="form-control" onChange={this.handleChange}>
						<option defaultValue selected>Select category</option>
						{
							this.state.adCategories.map(function(element) {
								return <option value={element.id}>{element.name}</option>
							})
						}
						</select>
					</div>
					
					<div className="form-group">
						<label>Item Location</label>
						<select name="adLocation" className="form-control" onChange={this.handleChange}>
						<option defaultValue selected>Select location</option>
						{
							this.state.locations.map(function(element) {
								return <option value={element.id}>{element.name}</option>
							})
						}
						</select>
					</div>
					
					<div className="form-group">
						<label>Sales Area</label>
						<select name="salesArea" className="form-control" onChange={this.handleChange}>
						<option defaultValue selected>Select sales area</option>
						{
							this.state.salesAreas.map(function(element) {
								return <option value={element}>{element}</option>
							})
						}
						</select>
					</div>
					
					<div className="form-group">
						<label>Price</label>
						<input name="price" type="text" ref="price" className="form-control" onChange={this.handleChange} />
					</div>

					<div className="form-group">
						<input type="submit" className="btn" value="Submit" />
					</div>
				</form>
				
				<div  className="form-horizontal">
					<div className="form-group">
						<label>Image</label>
						<div id="myId"></div>
					</div>
					<p>Drag and drop one or more images to upload.</p>
				</div>
				
			</div>
			<div className="col-md-6">
				<h2>Preview</h2>
				
				<h3>{this.state.adTitle}</h3>
				<br/>
				<p>{this.state.adText}</p>
				<dl className="dl-horizontal">
				  {typeof this.state.adCategory !== "undefined"  && <dt>Ad Category</dt>}
				  <dd>{typeof this.state.adCategory !== "undefined" && this.state.adCategory_text.replace("_", " ")}</dd>
				  {typeof this.state.adLocation !== "undefined" && <dt>Location</dt>}
				  <dd>{this.state.adLocation_text}</dd>
				  {typeof this.state.salesArea !== "undefined" && <dt>Sales Area</dt>}
				  <dd>{typeof this.state.salesArea !== "undefined" && this.state.salesArea_text.replace("_", " ")}</dd>
				  {this.state.price != '' && typeof this.state.price !== "undefined" && <dt>Price</dt>}
				  <dd>{this.state.price}</dd>
				</dl>
			</div>
		</div>
	);
	}
  }

  ReactDOM.render(<Create />, document.getElementById('container'));

</script>
   </jsp:attribute>
</t:wrapper>

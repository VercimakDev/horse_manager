import {Component, OnInit} from '@angular/core';
import {ToastrService} from 'ngx-toastr';
import {HorseService} from 'src/app/service/horse.service';
import {Horse, HorseSearch} from '../../dto/horse';
import {Owner} from '../../dto/owner';
import {Sex} from '../../dto/sex';
import {ActivatedRoute, Router} from '@angular/router';
import {debounceTime, of} from 'rxjs';
import {OwnerService} from '../../service/owner.service';

@Component({
  selector: 'app-horse',
  templateUrl: './horse.component.html',
  styleUrls: ['./horse.component.scss']
})
export class HorseComponent implements OnInit {
  search = false;
  horses: Horse[] = [];
  bannerError: string | null = null;
  searchHorse: HorseSearch = {
    name: undefined,
    description: undefined,
    bornBefore: undefined,
    sex: undefined,
  };
  constructor(
    private service: HorseService,
    private notification: ToastrService,
    private router: Router,
    private ownerService: OwnerService,
    private route: ActivatedRoute
  ) { }

  ngOnInit(): void {
    this.reloadHorses();
    this.getPathSearchParams();
  }

  reloadHorses() {
    const currentUrl = this.router.url;
    this.searchHorse.name = undefined;
    this.searchHorse.description = undefined;
    this.searchHorse.sex = undefined;
    this.searchHorse.bornBefore = undefined;
    this.searchHorse.owner = undefined;
    // Remove any path parameters from the current URL
    const urlWithoutParams = currentUrl.split(';')[0];
    // Navigate to the updated URL without any path parameters
    this.router.navigateByUrl(urlWithoutParams);
    this.service.getAll()
      .subscribe({
        next: data => {
          this.horses = data;
        },
        error: error => {
          console.error('Error fetching horses', error);
          this.bannerError = 'Could not fetch horses: ' + error.message;
          const errorMessage = error.status === 0
            ? 'Is the backend up?'
            : error.message.message;
          this.notification.error(errorMessage, 'Could Not Fetch Horses');
        }
      });
  }
  public delete(id: number){
    const observable = this.service.delete(id);
    observable.subscribe({
      next: data => {
        this.notification.success(`Horse with ID: ${id} successfully deleted.`);
        this.router.navigate(['/horses']);
        this.ngOnInit();
      },
      error: error => {
        console.error('Error deleting horse', error);
        this.notification.error('Could not delete this horse. Errorcode: ' + error.status + ', Errortext: ' + error.error.errors);
      }
    });
  }

  getPathSearchParams() {
    const name = this.route.snapshot.paramMap.get('name');
    const description = this.route.snapshot.paramMap.get('description');
    const dateOfBirth = this.route.snapshot.paramMap.get('dateOfBirth');
    const sex = this.route.snapshot.paramMap.get('sex');
    const ownerName = this.route.snapshot.paramMap.get('ownerName');
    if (name) {
      this.searchHorse.name = name;
    }
    if (description) {
      this.searchHorse.description = description;
    }
    if (dateOfBirth) {
      this.searchHorse.bornBefore = new Date(dateOfBirth);
    }
    if (sex) {
      if (sex.toLowerCase() === Sex.male.toString().toLowerCase()) {
        this.searchHorse.sex = Sex.male;
      }
      if (sex.toLowerCase() === Sex.female.toString().toLowerCase()) {
        this.searchHorse.sex = Sex.female;
      }
    }
    if (ownerName) {
      this.ownerService.searchByName(ownerName,1).subscribe({
        next: data => {
          this.searchHorse.owner = data[0];
        },
        error: error => {
          console.error('Error fetching owner', error);
          this.bannerError = 'Could not fetch owner: ' + error.message;
          const errorMessage = error.status === 0
            ? 'Is the backend up?'
            : error.message.message;
          this.notification.error(errorMessage, 'Could Not Fetch owner');
        }
      });
    }
  }

  ownerSuggestions = (input: string) => (input === '')
    ? of([])
    : this.ownerService.searchByName(input, 5);

  ownerName(owner: Owner | null): string {
    return owner
      ? `${owner.firstName} ${owner.lastName}`
      : '';
  }

  dateOfBirthAsLocaleDate(horse: Horse): string {
    return new Date(horse.dateOfBirth).toLocaleDateString();
  }

  searchHorses(){
    console.log('searchHorses invoked');
    const queryParams: any = {};
    if (this.searchHorse.name) {
      queryParams.name = this.searchHorse.name;
    }
    if (this.searchHorse.description) {
      queryParams.description = this.searchHorse.description;
    }
    if (this.searchHorse.bornBefore) {
      queryParams.bornBefore = this.searchHorse.bornBefore;
    }
    if (this.searchHorse.sex) {
      queryParams.sex = this.searchHorse.sex;
    }
    if (this.searchHorse.owner) {
      console.log('owner firstname: ' + this.formatOwnerName(this.searchHorse.owner));
      queryParams.ownerName = this.formatOwnerName(this.searchHorse.owner);
    }
    this.router.navigate([], {queryParams}).then();
    this.search = true;
    console.log('The ownerName: ' + this.formatOwnerName(this.searchHorse.owner));
    this.service.search(this.searchHorse).pipe(debounceTime(500))
      .subscribe({
        next: data => {
          console.log('Searchdata returned: ' + data);
          this.horses = data;
        },
        error: error => {
          console.error('Error finding matching horses', error);
          this.bannerError = 'Could not fetch horses: ' + error.message;
          const errorMessage = error.status === 0
            ? 'Is the backend up?'
            : error.message.message;
          this.notification.error(errorMessage, 'Could Not Fetch Horses');
        }
      });
  }
  public formatOwnerName(owner: Owner | null | undefined): string {
    return (owner == null)
      ? ''
      : `${owner.firstName} ${owner.lastName}`;
  }
}
